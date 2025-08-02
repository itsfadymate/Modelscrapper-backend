package guc.internship.modelscrapper.service.scraping.thangs;

import com.microsoft.playwright.*;

import guc.internship.modelscrapper.dto.search.SearchFilter;
import guc.internship.modelscrapper.model.ModelDetails;
import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.scraping.ScrapingService;
import guc.internship.modelscrapper.util.HttpHeadersUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ThangsScrapper implements ScrapingService {

    @Value("${Thangs.username}")
    private String email;
    @Value("${Thangs.password}")
    private String password;

    @Autowired
    @Qualifier("ThangsGoogle")
    private GooglePreviewDataStrategy googlePreviewer;

    @Autowired
    @Qualifier("ThangsJsoup")
    private JsoupPreviewDataStrategy jsoupPreviewer;


    private static final Logger logger = LoggerFactory.getLogger(ThangsScrapper.class);

    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm, SearchFilter filter) {
        List<ModelPreview> models= filter.isSourceToGoogle(this.getSourceName())? googlePreviewer.scrapePreviewData(searchTerm ,filter,getSourceName()) :
                                jsoupPreviewer.scrapePreviewData(searchTerm, filter,getSourceName());
        return models.stream().filter(filter::isValidModel).toList();
    }


    @Override
    public String getSourceName() {
        return "Thangs";
    }

    @Override
    public List<ModelPreview.File> getDownloadLinks(String id, String downloadPageUrl) {
        logger.debug("getting download links from thangs for: {}",downloadPageUrl);
        try(Playwright playwright = Playwright.create()){
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext(new Browser.NewContextOptions().setExtraHTTPHeaders(HttpHeadersUtil.DEFAULT_HEADERS));
            Page page = context.newPage();
            page.navigate(downloadPageUrl);

            page.waitForSelector("[class*=Model__desktop] [class*=DownloadLink]");
            ElementHandle downloadButton = page.querySelector("[class*=Model__desktop] [class*=DownloadLink]");

            Download download = page.waitForDownload(()->{
               downloadButton.click();
                logger.debug("clicked downloadButton");
                boolean loginOverlayAppeared = page.waitForSelector("[class*=StandardOverlay]", new Page.WaitForSelectorOptions().setTimeout(3000)) != null;

                if (loginOverlayAppeared) {
                    logger.debug("Login overlay appeared, attempting login...");
                    page.fill("input[id=email]", email);
                    page.fill("input[id=current-password]", password);
                    page.click("button[class*=Signin_Button]");
                 
                    page.waitForSelector("[class*=Model__desktop] [class*=DownloadLink]");
                    page.querySelector("[class*=Model__desktop] [class*=DownloadLink]").click();
                    logger.debug("Logged in and clicked downloadButton again");
                }
            });

            return List.of(new ModelPreview.File(download.suggestedFilename(), download.url()));
        }
        catch(Exception e){
            logger.error("couldn't get download links from Thangs: {}", e.getMessage());
            return List.of();
        }
    }

    @Override
    public ModelDetails getModelDetails(String id, String downloadPageUrl) {
        logger.debug("Getting model details from Thangs for: {}", downloadPageUrl);
        try {
            Document doc = org.jsoup.Jsoup.connect(downloadPageUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000)
                    .get();
            logger.debug("connected to {}",downloadPageUrl);
            Element markdownElement = doc.selectFirst(".markdown");
           // logger.debug("markDown: {}",markdownElement);
            String description = markdownElement != null ? markdownElement.toString() : "";

            String license = extractLicense(markdownElement);

            logger.debug("end of getModelDetails");
            return new ModelDetails(license, description, null);
        } catch (Exception e) {
            logger.error("Error getting model details from Thangs: {}", e.getMessage());
            return null;
        }
    }

    private String extractLicense(Element markdownElement) {
        if (markdownElement == null) {
            return null;
        }
        String text = markdownElement.text().toLowerCase();
        if (!text.contains("license")) return null;
        int idx = text.indexOf("license");
        int endIdx = text.indexOf('.', idx);
        if (endIdx == -1) endIdx = text.length();
        return text.substring(idx, endIdx).trim();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
