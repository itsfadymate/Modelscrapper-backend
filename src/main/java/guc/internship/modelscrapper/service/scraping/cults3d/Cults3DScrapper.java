package guc.internship.modelscrapper.service.scraping.cults3d;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import guc.internship.modelscrapper.client.cults3d.Cults3DApiClient;
import guc.internship.modelscrapper.dto.cults3d.Cults3DUrlResponse;
import guc.internship.modelscrapper.model.ModelDetails;
import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.scraping.ScrapePreviewDataStrategy;
import guc.internship.modelscrapper.service.scraping.ScrapingService;
import guc.internship.modelscrapper.util.HttpHeadersUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//The website API doesn't provide download APIs for "legal reasons"
@Service
public class Cults3DScrapper implements ScrapingService {

    private static final Logger logger = LoggerFactory.getLogger(Cults3DScrapper.class);

    @Autowired
    private Cults3DApiClient apiClient;

    @Value("${cults3D.cookies}")
    private String cookies;

    @Autowired
    @Qualifier("CultsGooglePreview")
    private ScrapePreviewDataStrategy googlePreviewer;

    @Autowired
    @Qualifier("CultsApiPreview")
    private ScrapePreviewDataStrategy apiPreviewer;


    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm,boolean showFreeOnly,boolean useGoogleEngine) {
        return useGoogleEngine? googlePreviewer.scrapePreviewData(searchTerm,showFreeOnly,getSourceName() ) : apiPreviewer.scrapePreviewData(searchTerm,showFreeOnly,getSourceName() );
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
            page.waitForSelector("div.mb-0\\.25 div.grid-cell--fit a.btn.btn-second");
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
        }catch (Exception e){
            logger.debug("Failed to getDownloadLinks for Cults {}",e.getMessage());
        }
        if (!files.isEmpty())
            return files;
        String query = String.format("""
                    {"query": "query Creation {
                                   creation(slug: \\"%s\\") {
                                       blueprints {
                                         fileName
                                       }
                                   }
                               }"
                    }
                    """,id).replaceAll("\n","");
        try{
            return apiClient.getModel(query).getData().getCreation().getFiles();
        }catch (Exception e){
            logger.error("fallback cults3d getDownloads strategy failed {}",e.getMessage());
            return List.of();
        }
    }

    @Override
    public ModelDetails getModelDetails(String id, String downloadPageUrl) {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
