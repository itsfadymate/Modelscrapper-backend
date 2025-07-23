package guc.internship.modelscrapper.service.scraping.grabcad;

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


@Service("GoogleGrabcad")
public class GooglePreviewDataStrategy implements ScrapePreviewDataStrategy {
    @Autowired
    private GoogleApiClient googleApi;

    @Value("${GrabCad.google.searchengine}")
    private String cx;

    private static final Logger logger = LoggerFactory.getLogger(GooglePreviewDataStrategy.class);

    private static final int PAGES_TO_SEARCH = 7;

    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm, boolean showFreeOnly, String websiteName) {
        logger.info("getting {} preview data from google", websiteName);
        List<ModelPreview> models = new ArrayList<>();
        for (int page = 0; page < PAGES_TO_SEARCH; page++) {
            List<GoogleItem> items = googleApi.searchTerm(cx, searchTerm, page * GoogleApiClient.RESULTS_PER_PAGE).getItems();
            for (GoogleItem item : items) {
                try {
                    if (!validProduct(item.getLink())) {
                        logger.debug("product link {} is not valid", item.getLink());
                        continue;
                    }


                    models.add(new ModelPreview()
                            .setId(item.getLink())
                            .setModelName(getModelNameFromTitle(item.getTitle()))
                            .setImageLink(item.getImageUrl())
                            .setWebsiteLink(item.getLink())
                            .setWebsiteName(websiteName)
                            .setPrice("0")
                    );
                } catch (Exception e) {
                    logger.error("something wrong happened while analyzing {}, exception {}", item.getLink(),e.getMessage());
                }
            }
        }
        return models;
    }


    private boolean validProduct(String link) {
        return link.matches("https://grabcad.com/library/[a-zA-Z0-9\\-]+");
    }

    private String getModelNameFromTitle(String title) {
        String removedSuffix = title.contains("|") ? title.substring(0, title.indexOf("|")) : title;
        return  removedSuffix.contains("...") ? removedSuffix.substring(0, removedSuffix.indexOf("...")) : removedSuffix;
    }

}
