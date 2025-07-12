package guc.internship.modelscrapper.service.scraping;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import guc.internship.modelscrapper.model.ModelPreview;
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
import java.util.Map;

@Service
public class Printables implements ScrapingService{
    private static final Logger logger = LoggerFactory.getLogger(Printables.class);

    @Value("${Printables.base.url}")
    private String baseUrl;

    private final static String ordering ="makes_count";
    private final static Map<String, String> headers = Map.ofEntries(
        Map.entry("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7"),
        Map.entry("Accept-Encoding", "gzip, deflate, br, zstd"),
        Map.entry("Accept-Language", "en-US,en;q=0.9,de-DE;q=0.8,de;q=0.7"),
        Map.entry("Connection", "keep-alive"),
        Map.entry("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36"),
        Map.entry("Sec-CH-UA-Platform", "\"Windows\""),
        Map.entry("Sec-CH-UA-Mobile", "?0"),
        Map.entry("Sec-CH-UA", "\"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"138\", \"Google Chrome\";v=\"138\""),
        Map.entry("Sec-Fetch-Dest", "document"),
        Map.entry("Sec-Fetch-Mode", "navigate"),
        Map.entry("Sec-Fetch-Site", "same-origin"),
        Map.entry("Sec-Fetch-User", "?1")
);

    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm, boolean showFreeOnly) {
        logger.debug("Scrapping printables for preview data");
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext(new Browser.NewContextOptions().setExtraHTTPHeaders(headers));
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
        List<ModelPreview.File> files = new ArrayList<>();
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            BrowserContext context = browser.newContext(new Browser.NewContextOptions().setExtraHTTPHeaders(headers));
            Page page = context.newPage();
            page.navigate(downloadPageUrl);
            page.waitForSelector("[data-testid=show-download-files]");
            page.click("[data-testid=show-download-files]");


            page.waitForSelector("[data-testid=download-file]");
            Download download = page.waitForDownload(() -> {
                page.click("[data-testid=download-file]");
            });
            String url = download.url();
            String suggestedFilename = download.suggestedFilename();
            files.add(new ModelPreview.File(suggestedFilename, url));
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
