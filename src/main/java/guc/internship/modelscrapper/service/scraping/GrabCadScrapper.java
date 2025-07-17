package guc.internship.modelscrapper.service.scraping;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.util.HttpHeadersUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class GrabCadScrapper implements ScrapingService {
    
    private static final Logger logger = LoggerFactory.getLogger(GrabCadScrapper.class);

    private static final int[] PER_PAGE_VALUES = {24,48,100};

    @Value("${GrabCad.search.url}")
    private String GRABCAD_SEARCH_URL;

    @Value("${GrabCad.cookies}")
    private String cookies;

    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm,boolean showFreeOnly) {
        logger.debug("Scraping GrabCAD for: {}", searchTerm);

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            int pageNo =1;
            String searchUrl = String.format(GRABCAD_SEARCH_URL +"?page=%d&per_page=%d&time=all_time&sort=recent&categories=3d-printing&query=%s", pageNo,PER_PAGE_VALUES[1],searchTerm);
            logger.debug(searchUrl);
            logger.debug("Navigating to: {}", searchUrl);

            page.navigate(searchUrl);
            page.waitForLoadState(LoadState.NETWORKIDLE);

            String pageHtml = page.content();
            logger.debug("Retrieved GrabCad HTML content, length: {}", pageHtml.length());

            browser.close();
            Document doc = Jsoup.parse(pageHtml);

            Elements cards = doc.getElementsByClass("modelCard");

            List<ModelPreview> models = new ArrayList<>();
            for (Element card : cards){
                ModelPreview preview = extractCardData(card);
                if (preview != null) {
                    models.add(preview);
                }
            }

            return models;
        } catch (Exception e) {
            logger.error("Error scraping GrabCAD for search term: {}", searchTerm, e);
            return List.of();
        }
    }

    private ModelPreview extractCardData(Element card){
        try {
            Element imageElement = card.selectFirst(".previewImage");
            String imageSrc = null;
            if (imageElement != null) {
                imageSrc = imageElement.attr("src");
                if (imageSrc == null || imageSrc.isEmpty()) {
                    imageSrc = imageElement.attr("ng-src");
                }
            }

            Element linkElement = card.selectFirst(".imageArea .modelLink");
            if (linkElement == null) {
                linkElement = card.selectFirst(".modelName a");
            }
            String modelLink = null;
            if (linkElement != null) {
                String href = linkElement.attr("href");
                modelLink = href.startsWith("/") ? "https://grabcad.com" + href : href;
            }
            

            Element nameElement = card.selectFirst(".modelName .name");
            String modelName = null;
            if (nameElement != null) {
                modelName = nameElement.text().trim();
            }

            Element countsSection = card.selectFirst(".countsSection");
            String likesCount ="0",commentCount="0";
            if (countsSection!=null){
                Element likeSpan = countsSection.selectFirst(".gc-icon-like + .text ");
                if (likeSpan!=null)
                    likesCount = likeSpan.text();
                Element commentSpan = countsSection.selectFirst(".gc-icon-comment + .text ");
                if (commentSpan!=null)
                    commentCount = commentSpan.text();
            }

            if (modelName != null && !modelName.isEmpty() && modelLink != null) {
                return new ModelPreview()
                        .setImageLink(imageSrc)
                        .setModelName(modelName)
                        .setWebsiteName(this.getSourceName())
                        .setWebsiteLink(modelLink)
                        .setLikesCount(likesCount)
                        .setCommentsCount(commentCount);
            }

            logger.warn("Failed to extract essential data - name: {}, link: {}", modelName, modelLink);
        } catch (Exception e) {
            logger.error("Error extracting card data", e);
        }
        return null;
    }

    @Override
    public String getSourceName() {
        return "GrabCad";
    }

    @Override
    public List<ModelPreview.File> getDownloadLinks(String id, String downloadPageUrl) {
        logger.debug("getting grabcad download links from {}",downloadPageUrl);
        List<ModelPreview.File> resultFiles = new ArrayList<>();
        Page page = null;
        try(Playwright playwright = Playwright.create()){
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            HashMap<String,String> headers = new HashMap<>(HttpHeadersUtil.DEFAULT_HEADERS);
            headers.put("Cookie",cookies);
            BrowserContext context = browser.newContext(new Browser.NewContextOptions().setExtraHTTPHeaders(headers));

            page = context.newPage();
            page.navigate(downloadPageUrl,new Page.NavigateOptions().setTimeout(300000));
            logger.debug("navigating to {}",downloadPageUrl);

            try {
                Locator consentButton = page.locator("button:has-text(\"Accept\")").first();
                logger.debug("trying to click consent button, html {}",page.content());
                if (consentButton.isVisible()) {
                    consentButton.click();
                    logger.debug("clicked consent button");
                }
                logger.debug("end of cookie consent overlay try block");
            } catch (Exception e) {
                logger.debug("No consent dialog found or error clicking consent: {}", e.getMessage());
            }

            page.waitForSelector(".tbody .row.ng-scope");
            List<ElementHandle> elements = page.querySelectorAll(".tbody .row.ng-scope");

            for (ElementHandle e : elements){
                e.click();
                page.waitForSelector("span.actions");
                ElementHandle downloadButton = page.querySelector("span.downloadText");
                Download download = page.waitForDownload(()->{
                    downloadButton.click();
                    logger.debug("clicked download button");
                });
                resultFiles.add(new ModelPreview.File(download.suggestedFilename(),download.url()));
                page.querySelector("a.close.link").click();
                logger.debug("clicked close button");
            }
            browser.close();
        } catch (Exception e) {
            logger.debug("couldnt get grabcad download links {} ",e.getMessage());
            if (page!=null)
                logger.debug(page.content());
        }
        return resultFiles;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
