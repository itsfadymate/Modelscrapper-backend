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
import java.util.List;


@Service
public class Printables implements ScrapingService{
    private static final Logger logger = LoggerFactory.getLogger(Printables.class);

    @Value("${Printables.base.url}")
    private String baseUrl;

    private final static String ordering ="makes_count";


    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm, boolean showFreeOnly) {
        logger.debug("Scrapping printables for preview data");
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext(new Browser.NewContextOptions().setExtraHTTPHeaders(HttpHeadersUtil.DEFAULT_HEADERS));
            Page page = context.newPage();
            String searchUrl = String.format(baseUrl +"search/models?q=%s&ordering=%s",searchTerm,ordering);
            logger.debug(searchUrl);
            logger.debug("Navigating to: {}", searchUrl);

            page.navigate(searchUrl);
            page.waitForLoadState(LoadState.NETWORKIDLE);

            String pageHtml = page.content();
            logger.debug("Retrieved printables HTML content, length: {}", pageHtml.length());

            browser.close();
            Document doc = Jsoup.parse(pageHtml);

            Elements cards = doc.select("[data-testid=model]");

            List<ModelPreview> models = new ArrayList<>();
            for (Element card : cards){
                ModelPreview preview = extractCardData(card);
                if (preview != null) {
                    models.add(preview);
                }
            }

            return models;
        } catch (Exception e) {
            logger.error("Error scraping printables for search term: {}", searchTerm, e);
            return List.of();
        }
    }

    private ModelPreview extractCardData(Element card) {
        Element nameAndLinkElement = card.selectFirst("h5 a");
        try{
            String modelName=null,modelLink=null;
            if (nameAndLinkElement!=null){
                modelName = nameAndLinkElement.text();
                modelLink = nameAndLinkElement.attr("href");
                modelLink = modelLink.startsWith("/")? baseUrl+modelLink.substring(1) : modelLink;
            }
            if (modelName==null)throw new Exception("couldn't extract crucial modelName or link");

            String imageUrl=null;
            Element img = card.selectFirst(".image-inside [class*=svelte] ");
            if (img!=null)
                imageUrl = img.attr("src");

            String likesCount="0";
            Element likesSpan = card.selectFirst("[data-testid=like-count]");
            if (likesSpan!=null)
                likesCount = likesSpan.text();
            boolean isFeatured = card.selectFirst(".featured-badge")!=null;

            return new ModelPreview()
                    .setId(modelLink)
                    .setModelName(modelName)
                    .setWebsiteLink(modelLink)
                    .setImageLink(imageUrl)
                    .setWebsiteName(this.getSourceName())
                    .setLikesCount(likesCount)
                    .setFeatured(isFeatured)
                    .setPrice("0");
        } catch (Exception e) {
            logger.debug("failed to extract Printables card info",e);
            return null;
        }
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
            page.navigate(downloadPageUrl);
            page.waitForSelector("[data-testid=show-download-files]");
            page.click("[data-testid=show-download-files]");


            page.waitForSelector("[data-testid=download-file]");
            List<ElementHandle> downloadButtons = page.querySelectorAll("[data-testid=download-file]");
            for (ElementHandle button : downloadButtons) {
                Download download = page.waitForDownload(() -> {button.click();});
                files.add(new ModelPreview.File(download.suggestedFilename(),  download.url()));
            }
            browser.close();
        } catch (Exception e) {
            logger.error("Failed to get download links from Printables", e);
        }
        return files;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
