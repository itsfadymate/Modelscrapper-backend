package guc.internship.modelscrapper.service.scraping.myminifactory;

import guc.internship.modelscrapper.client.google.GoogleApiClient;
import guc.internship.modelscrapper.client.myminifactory.MyMiniFactoryApiClient;
import guc.internship.modelscrapper.dto.google.GoogleSearchResponse;
import guc.internship.modelscrapper.dto.myminifactory.MyMiniFactoryDTO;
import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.scraping.ScrapePreviewDataStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("miniFactoryGooglePreviewer")
public class GooglePreviewDataStrategy implements ScrapePreviewDataStrategy {

    private static final Logger logger = LoggerFactory.getLogger(GooglePreviewDataStrategy.class);
    @Autowired
    private MyMiniFactoryApiClient miniFactoryApiClient;

    @Autowired
    private GoogleApiClient googleApiClient;

    @Value("${MyMiniFactory.google.searchengine}")
    private String cx;


    private final static int PAGES_TO_FETCH = 4;


    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm, boolean showFreeOnly, String websiteName) {
        logger.debug("scraping myminifactory preview data from google");
        GoogleSearchResponse response = null;
        List<ModelPreview> modelPreviews = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        for (int page = 0; page < PAGES_TO_FETCH; page++) {
            try {
                response = googleApiClient.searchTerm(cx, searchTerm, page * GoogleApiClient.RESULTS_PER_PAGE);
                ids = response.getLinks().stream().map(link -> link.substring(link.lastIndexOf("-") + 1)).toList();
                for (String id : ids) {
                    try {
                        MyMiniFactoryDTO dto = miniFactoryApiClient.getObject(id);
                        modelPreviews.add(new ModelPreview()
                                .setId(String.valueOf(dto.getId()))
                                .setImageLink(dto.getPreviewImageUrl())
                                .setModelName(dto.getName())
                                .setWebsiteName(websiteName)
                                .setWebsiteLink(dto.getUrl())
                                .setMakesCount(dto.getMakesCount())
                                .setFiles(dto.getFiles())
                                .setLikesCount(dto.getLikesCount())
                                .setPrice(dto.getPrice())
                        );
                    } catch (Exception e) {
                        logger.debug("couldn't fetch object with id {} exception: {}", id, e.getMessage());
                    }
                }
            } catch (Exception e) {
                logger.debug("something went wrong while getting previewData from google {}", e.getMessage());
                logger.debug("    google response {}", response);
                logger.debug("    ids: {}", ids);
            }
        }
        return modelPreviews;
    }
}
