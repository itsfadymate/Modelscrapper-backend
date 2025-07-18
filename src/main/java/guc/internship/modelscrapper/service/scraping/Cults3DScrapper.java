package guc.internship.modelscrapper.service.scraping;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import guc.internship.modelscrapper.client.cults3d.Cults3DApiClient;
import guc.internship.modelscrapper.dto.cults3d.Cults3DDTO;
import guc.internship.modelscrapper.dto.cults3d.Cults3DSearchResponse;
import guc.internship.modelscrapper.dto.cults3d.Cults3DUrlResponse;
import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.util.HttpHeadersUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

//The website API doesn't provide download APIs for "legal reasons"
@Service
public class Cults3DScrapper implements ScrapingService {

    private static final Logger logger = LoggerFactory.getLogger(Cults3DScrapper.class);

    @Autowired
    private Cults3DApiClient apiClient;

    @Value("${cults3D.cookies}")
    private String cookies;


    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm,boolean showFreeOnly) {
        logger.debug("Searching Cults3D API for: {}", searchTerm);

        String query = String.format("""
                {"query": "query Search {
                            search(query: \\"%s\\", onlySafe: true, sort: BY_DOWNLOADS) {
                                name(locale: EN)
                                url(locale: EN)
                                illustrationImageUrl(version: DEFAULT)
                                blueprints {
                                    fileName
                                }
                                description(locale: EN)
                                details(locale: EN)
                                price(currency: EUR) {
                                    formatted(locale: EN)
                                }
                                makes {
                                    id
                                }
                                comments {
                                     text
                                }
                                likesCount
                                featured
                                slug
                            }
                        }"
                }
                """,searchTerm).replaceAll("\n","");



        //logger.debug("Cults3D GraphQL query: "+ query);
        
        try {

            Cults3DSearchResponse searchResponse = apiClient.searchCults(query);
            //logger.debug("Raw API response: {}", searchResponse);

            List<Cults3DDTO> response = searchResponse.getSearchResults();

            if (response == null) {
                logger.warn("getSearchResults() returned null");
                return List.of();
            }
            

            List<ModelPreview> previews = response.stream().filter(dto-> {
                if (showFreeOnly){
                 return isFree(dto.getFormattedPrice());
                }
                return true;
            }).map(dto ->
                 new ModelPreview()
                         .setId(dto.getSlug())
                         .setImageLink(dto.getIllustrationImageUrl())
                         .setModelName(dto.getName())
                         .setWebsiteName(this.getSourceName())
                         .setWebsiteLink(dto.getUrl())
                         .setPrice(dto.getFormattedPrice())
                         .setFiles(dto.getFiles())
                         .setMakesCount(dto.getMakeCount())
                         .setLikesCount(dto.getLikesCount())
                         .setCommentsCount(String.valueOf(dto.getCommentCount()))
                         .setFeatured(dto.isFeatured())
            ).collect(Collectors.toList());
            
            logger.debug("Found {} results from Cults3D API", previews.size());
            return previews;
        } catch (Exception e) {
            logger.error("Error calling Cults API for search term: {}", searchTerm, e);
            return List.of();
        }
    }
    private boolean isFree(String formattedPrice){
        if (formattedPrice == null || formattedPrice.trim().isEmpty()) {
            return false;
        }
        try {
            String numericPrice = formattedPrice.replaceAll("[^0-9.]", "");
            double price = Double.parseDouble(numericPrice);
            return price == 0;
        } catch (NumberFormatException e) {
            logger.warn("Could not parse price: {}", formattedPrice);
            return false;
        }
    }



    @Override
    public String getSourceName() {
        return "Cults3D";
    }

    @Override
    public List<ModelPreview.File> getDownloadLinks(String id, String downloadPageUrl) {
        logger.debug("getting download links from  {}",downloadPageUrl);
        if (downloadPageUrl.isEmpty()){
            String query = String.format("""
                    {"query": "query Creation {
                                   creation(slug: \\"%s\\") {
                                       url
                                   }
                               }"
                    }
                    """,id).replaceAll("\n","");
            Cults3DUrlResponse urlResponse = apiClient.getUrl(query);
            downloadPageUrl = urlResponse.getUrl();
            logger.debug("got the url from the API");
        }

        List<ModelPreview.File> files = new ArrayList<>();
        try(Playwright playwright= Playwright.create();) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            HashMap<String, String> finalHeaders = new HashMap<>(HttpHeadersUtil.DEFAULT_HEADERS);
            finalHeaders.put("Cookie", cookies);
            BrowserContext context = browser.newContext(new Browser.NewContextOptions().setExtraHTTPHeaders(finalHeaders));
            Page page = context.newPage();
            page.navigate(downloadPageUrl,new Page.NavigateOptions()
                    .setTimeout(130000));
            page.waitForSelector("form.button_to .btn-group--large ");
            page.querySelector("form.button_to .btn-group--large ").click();
            logger.debug("clicked the download button");
            //TODO: Login is bypassed through cookies which is not a sustainable solution
            List<ElementHandle> sliceButtons = page.querySelectorAll("div.mb-0\\.25 div.grid-cell--fit a.btn.btn-second");
            for (ElementHandle slice : sliceButtons){
                   slice.click();
                   page.waitForSelector(".mt-1 a.btn.btn-second");
                   ElementHandle downloadButton = page.querySelector(".mt-1 a.btn.btn-second");
                   Download download = page.waitForDownload(downloadButton::click);
                   logger.debug("clicked download for {} ",download.suggestedFilename());
                   files.add(new ModelPreview.File(download.suggestedFilename(),  download.url()));
                   ElementHandle closeButton = page.querySelector(".dialog__header .btn-close");
                   closeButton.click();
                   page.waitForSelector(".dialog__header .btn-close", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN));
            }
            logger.debug("end of slice buttons");
            browser.close();
            return files;
        }catch (Exception e){
            logger.debug("Failed to getDownloadLinks for Cults {}",e.getMessage());
        }

        return List.of();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
