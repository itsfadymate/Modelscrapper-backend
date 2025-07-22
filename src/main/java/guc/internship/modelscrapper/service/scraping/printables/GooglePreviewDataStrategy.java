package guc.internship.modelscrapper.service.scraping.printables;

import guc.internship.modelscrapper.client.google.GoogleApiClient;
import guc.internship.modelscrapper.dto.google.GoogleItem;
import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.scraping.ScrapePreviewDataStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("GooglePrintables")
public class GooglePreviewDataStrategy implements ScrapePreviewDataStrategy {

    @Autowired
    private GoogleApiClient googleApi;

    @Value("${Printables.google.searchengine}")
    private String cx;

    private static final Logger logger = LoggerFactory.getLogger(GooglePreviewDataStrategy.class);

    private static final int PAGES_TO_SEARCH = 7;

    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm, boolean showFreeOnly, String websiteName) {
        logger.info("getting printables preview data from google");
        List<ModelPreview> models = new ArrayList<>();
        for (int page=0;page<PAGES_TO_SEARCH;page++){
            List<GoogleItem> items = googleApi.searchTerm(cx,searchTerm,page*GoogleApiClient.RESULTS_PER_PAGE).getItems();
            for (GoogleItem item : items){
                if (!validProduct(item.getLink())){
                    logger.debug("product link {} is not valid",item.getLink());
                    continue;
                }
                models.add(new ModelPreview()
                        .setId(item.getLink())
                        .setModelName(getModelName(item.getTitle()))
                        .setImageLink(item.getImageUrl())
                        .setWebsiteLink(item.getLink())
                        .setWebsiteName(websiteName)
                        .setPrice("0")
                );
            }
        }
        return models;
    }

    private boolean validProduct(String link){
        return link.matches("https://www.printables.com/model/[0-9a-zA-Z\\-]*");
    }

    private String getModelName(String title){
        String x= title.substring(0, title.contains("|") ?title.indexOf("|"): title.length());
        return x.substring(0, x.contains("...") ? x.indexOf("..."): x.length());
    }
}
