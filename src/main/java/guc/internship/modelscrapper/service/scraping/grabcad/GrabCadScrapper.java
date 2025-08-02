package guc.internship.modelscrapper.service.scraping.grabcad;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;

import guc.internship.modelscrapper.dto.search.SearchFilter;
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

@Service
public class GrabCadScrapper implements ScrapingService {
    
    private static final Logger logger = LoggerFactory.getLogger(GrabCadScrapper.class);


    @Value("${GrabCad.cookies}")
    private String cookies;

    @Autowired
    @Qualifier("GoogleGrabcad")
    private ScrapePreviewDataStrategy googlePreviewer;

    @Autowired
    @Qualifier("PlaywrightGrabcad")
    private ScrapePreviewDataStrategy playwrightPreviewer;
    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm, SearchFilter filter) {
        List<ModelPreview> models = filter.isSourceToGoogle(this.getSourceName())? googlePreviewer.scrapePreviewData(searchTerm, filter,getSourceName()):
                playwrightPreviewer.scrapePreviewData(searchTerm, filter,getSourceName());
        return models.stream().filter(filter::isValidModel).toList();
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
                // Wait for the overlay to appear (if it does)
                page.waitForSelector("div.fc-dialog-overlay", new Page.WaitForSelectorOptions().setTimeout(5000));
                // Try to click the accept button if visible
                ElementHandle acceptBtn = page.querySelector("button.fc-vendor-preferences-accept-all");
                if (acceptBtn != null && acceptBtn.isVisible()) {
                    acceptBtn.click();
                    logger.debug("Clicked consent accept button");
                    // Wait for overlay to disappear
                    page.waitForSelector("div.fc-dialog-overlay", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.DETACHED).setTimeout(10000));
                    logger.debug("Consent overlay dismissed");
                } else {
                    logger.debug("Consent accept button not found or not visible");
                }
            } catch (Exception e) {
                logger.debug("No consent dialog found or error clicking consent: {}", e.getMessage());
            }

            try {
                Locator modalCloseButton = page.locator("img.gc-shared-homepagev2modal__hidebutton").first();
                if (modalCloseButton.isVisible()) {
                    modalCloseButton.click();
                    logger.debug("Closed homepage modal popup");
                }
            } catch (Exception e) {
                logger.debug("couldn't click on hide modal button");
            }

           /* page.navigate(downloadPageUrl, new Page.NavigateOptions().setTimeout(300000));
            logger.debug("Renavigated to {}", downloadPageUrl);
*/
            page.waitForSelector(".tbody .row.ng-scope");
            List<ElementHandle> elements = page.querySelectorAll(".tbody .row.ng-scope");

            for (ElementHandle e : elements){
                e.click();
                page.waitForSelector("span.actions");
                page.waitForSelector("span.downloadText", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
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
        }
        return resultFiles;
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
