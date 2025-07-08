package guc.internship.modelscrapper.service;

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

@Service
public class GrabCadScrapper implements ScrapingService {
    
    private static final Logger logger = LoggerFactory.getLogger(GrabCadScrapper.class);

    private static final int[] PER_PAGE_VALUES = {24,48,100};

    @Value("${GrabCad.search.url}")
    private String GRABCAD_SEARCH_URL;

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

            if (modelName != null && !modelName.isEmpty() && modelLink != null) {
                return new ModelPreview()
                        .setImageLink(imageSrc)
                        .setModelName(modelName)
                        .setWebsiteName(this.getSourceName())
                        .setWebsiteLink(modelLink);
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
    public List<ModelPreview.File> getDownloadLinks(String id) {
        return List.of();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
