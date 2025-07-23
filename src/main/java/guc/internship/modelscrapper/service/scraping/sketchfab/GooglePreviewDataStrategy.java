package guc.internship.modelscrapper.service.scraping.sketchfab;

import guc.internship.modelscrapper.client.google.GoogleApiClient;
import guc.internship.modelscrapper.client.sketchfab.SketchfabApiClient;
import guc.internship.modelscrapper.dto.google.GoogleSearchResponse;
import guc.internship.modelscrapper.dto.sketchfab.SketchfabSearchObject;
import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.scraping.ScrapePreviewDataStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("sketchFabGooglePreviewer")
public class GooglePreviewDataStrategy implements ScrapePreviewDataStrategy {

    private static final Logger logger = LoggerFactory.getLogger(GooglePreviewDataStrategy.class);
    @Autowired
    private SketchfabApiClient sketchFabApiClient;
    @Autowired
    private GoogleApiClient googleApiClient;

    @Value("${Sketchfab.google.searchengine}")
    private String cx;

    private static final int PAGES_TO_SEARCH = 2;

    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm, boolean showFreeOnly, String websiteName) {
        logger.debug("scraping google for {} models", websiteName);
        List<ModelPreview> models = new ArrayList<>();
        for (int page = 0; page < PAGES_TO_SEARCH; page++) {
            try {
                GoogleSearchResponse response = googleApiClient.searchTerm(cx,searchTerm,page*GoogleApiClient.RESULTS_PER_PAGE);
                List<String> uids = response.getLinks().stream().map(this::getUidFromLink).filter(this::isValidUid).toList();
                for (String uid : uids){
                    SketchfabSearchObject model = sketchFabApiClient.getModel(uid);
                    models.add(new ModelPreview()
                                    .setModelName(model.getName())
                                    .setId(model.getId())
                                    .setWebsiteName(websiteName)
                                    .setImageLink(model.getPreviewImageUrl())
                                    .setLikesCount(model.getLikesCount())
                                    .setCommentsCount(model.getCommentCount())
                                    .setFeatured(model.isFeatured())
                                    .setPrice(model.isDownloadable()? "0" : "paid")
                    );
                }
            } catch (Exception e) {
                logger.error("couldn't scrape google page {} for {} data. {}",page+1,websiteName,e.getMessage());
            }
        }
        return models;
    }

    private String getUidFromLink(String link){
        return link.substring(link.lastIndexOf("-")+1);
    }

    private boolean isValidUid(String id){
        return id.matches("[a-zA-Z0-9]+");
    }
}
