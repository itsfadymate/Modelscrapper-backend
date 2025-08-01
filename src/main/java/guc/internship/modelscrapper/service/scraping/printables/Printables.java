package guc.internship.modelscrapper.service.scraping.printables;

import com.microsoft.playwright.*;
import guc.internship.modelscrapper.model.ModelDetails;
import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.scraping.ScrapingService;
import guc.internship.modelscrapper.util.HttpHeadersUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class Printables implements ScrapingService {
    private static final Logger logger = LoggerFactory.getLogger(Printables.class);

    @Autowired
    @Qualifier("PlaywrightPrintables")
    private PlaywrightPreviewDataStrategy playwrightPreviewer;


    @Autowired
    @Qualifier("GooglePrintables")
    private GooglePreviewDataStrategy googlePreviewer;

    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm, boolean showFreeOnly,boolean useGoogleEnine) {
        return useGoogleEnine? googlePreviewer.scrapePreviewData(searchTerm,showFreeOnly,getSourceName() ) : playwrightPreviewer.scrapePreviewData(searchTerm,showFreeOnly,getSourceName() );
    }



    @Override
    public String getSourceName() {
        return "Printables";
    }

    @Override
    public List<ModelPreview.File> getDownloadLinks(String id, String downloadPageUrl) {
        logger.debug("Fetching download links for page {}",downloadPageUrl);
        List<ModelPreview.File> files = new ArrayList<>();
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext(new Browser.NewContextOptions().setExtraHTTPHeaders(HttpHeadersUtil.DEFAULT_HEADERS));
            Page page = context.newPage();
            executeDownloadSteps(downloadPageUrl, page, files);
            browser.close();
        } catch (Exception e) {
            logger.error("Failed to get download links from Printables", e);
        }
        return files;
    }

    private  void executeDownloadSteps(String downloadPageUrl, Page page, List<ModelPreview.File> files) {
        page.navigate(downloadPageUrl +"/files");
        page.waitForSelector("[data-testid=download-file]");
        List<ElementHandle> downloadButtons = page.querySelectorAll("[data-testid=download-file]");
        for (ElementHandle button : downloadButtons) {
            Download download = page.waitForDownload(() -> {
                button.click();
                logger.debug("clicked download button");
            });
            files.add(new ModelPreview.File(download.suggestedFilename(),  download.url()));
        }
    }

    @Override
    public ModelDetails getModelDetails(String id, String downloadPageUrl) {
        logger.debug("Fetching printables model details with url {}",downloadPageUrl);
        List<ModelPreview.File> files = new ArrayList<>();
        String description = null;
        String license =null;
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext(new Browser.NewContextOptions().setExtraHTTPHeaders(HttpHeadersUtil.DEFAULT_HEADERS));
            Page page = context.newPage();
            page.navigate(downloadPageUrl);
            try{
                page.waitForSelector(".svelte-aasxxg");
                Document pageDoc = Jsoup.parse( page.content());
                Element licenseElement = pageDoc.select(".svelte-aasxxg a").last();
                logger.debug("license element: {}",licenseElement==null);
                logger.debug("page {}", pageDoc.getElementsByClass("tab-content").html());
                license = licenseElement==null? null : licenseElement.html();
            } catch (Exception e) {
                logger.debug("failed to get license {}",e.getMessage());
            }
            try {
                Document pageDoc = Jsoup.parse( page.content());
                description = pageDoc.getElementsByClass("user-inserted").html();
            }catch (Exception e){
                logger.debug("failed to get description {}",e.getMessage());
            }
            executeDownloadSteps(downloadPageUrl, page, files);
            browser.close();
        } catch (Exception e) {
            logger.error("Failed to get model details from Printables", e);
        }
        return new ModelDetails(license,description,files);
    }


    @Override
    public boolean isEnabled() {
        return true;
    }
}
