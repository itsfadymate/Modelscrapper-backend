package guc.internship.modelscrapper.service;

import guc.internship.modelscrapper.model.Model3D;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class ThingiversePreviewPreviewScrapingService implements PreviewScrapingService {

    private static final Logger logger = LoggerFactory.getLogger(ThingiversePreviewPreviewScrapingService.class);

    @Override
    public List<Model3D> scrape(String searchTerm) {
        logger.debug("Starting scrape for search term: {}", searchTerm);
        List<Model3D> results = new ArrayList<>();
        
        try {
            String url = "https://www.thingiverse.com/search?q=" + searchTerm.replace(" ", "+");
            logger.debug("Scraping URL: {}", url);
            
            Element nextPageButton;
            ArrayList<Model3D> models = new ArrayList<>();
            int pageCount = 0;
            
            do {
                pageCount++;
                logger.debug("Scraping page {}: {}", pageCount, url);
                
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .get();
                logger.debug(doc.body().html());

                List<Model3D> pageModels = documentScrape(doc);
                logger.debug("Found {} models on page {}", pageModels.size(), pageCount);
                models.addAll(pageModels);
                
                nextPageButton = doc.selectFirst("[aria-label='Next page']");
                url = nextPageButton == null ? "" : nextPageButton.absUrl("href");
                logger.debug("Next page button exists: {}, disabled: {}", 
                    nextPageButton != null, 
                    nextPageButton != null && nextPageButton.hasAttr("disabled"));
                
            } while (nextPageButton != null && !nextPageButton.hasAttr("disabled"));
            
            logger.info("Total models scraped from Thingiverse: {}", models.size());
            return models;
            
        } catch (IOException e) {
            logger.error("Error scraping Thingiverse: {}", e.getMessage(), e);
        }
        
        return results;
    }

    private List<Model3D> documentScrape(Document doc) {
        Elements cards = doc.getElementsByClass("ItemCardContainer__itemCard--GGbYM");
        logger.debug("Found {} card elements with class 'ItemCardContainer__itemCard--GGbYM'", cards.size());
        
        List<Model3D> models = new ArrayList<>();
        for (Element c : cards) {
            try {
                Model3D model = cardScrape(c);
                models.add(model);
                logger.debug("Successfully scraped model: {}", model.getModelname());
            } catch (Exception e) {
                logger.warn("Couldn't parse a model card: {}", e.getMessage());
            }
        }
        return models;
    }

    private Model3D cardScrape(Element card) {
        logger.debug("Parsing card element");
        Model3D model = new Model3D();
        
        Element lowerATag = card.getElementsByClass("ItemCardHeader__itemCardHeader--cPULo").first();
        if (lowerATag != null) {
            model.setModelname(lowerATag.text());
            model.setWebsitelink(lowerATag.absUrl("href"));
            logger.debug("Found model name: {}, URL: {}", model.getModelname(), model.getWebsitelink());
        } else {
            logger.warn("Could not find ItemCardHeader__itemCardHeader--cPULo element");
        }
        
        model.setWebsitename(this.getSourceName());
        
        Elements img = card.getElementsByClass("ItemCardContent__itemCardContentImage--uzD0A");
        if (!img.isEmpty()) {
            model.setImagelink(img.attr("src"));
            logger.debug("Found image URL: {}", model.getImagelink());
        } else {
            logger.warn("Could not find ItemCardContent__itemCardContentImage--uzD0A element");
        }
        
        return model;
    }

    @Override
    public String getSourceName() {
        return "Thingiverse";
    }


    //set to false when the scrapper inevitably breaks down :)
    @Override
    public boolean isEnabled() {
        boolean enabled = true;
        logger.debug("Thingiverse scraper is enabled: {}", enabled);
        return enabled;
    }
}