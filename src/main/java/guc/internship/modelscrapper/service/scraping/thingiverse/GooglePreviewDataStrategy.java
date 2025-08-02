package guc.internship.modelscrapper.service.scraping.thingiverse;

import guc.internship.modelscrapper.client.google.GoogleApiClient;
import guc.internship.modelscrapper.client.thingiverse.ThingiverseApiClient;
import guc.internship.modelscrapper.dto.google.GoogleSearchResponse;
import guc.internship.modelscrapper.dto.search.SearchFilter;
import guc.internship.modelscrapper.dto.thingiverse.ThingiverseThing;
import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.localfilehosting.LocalFileHostingService;
import guc.internship.modelscrapper.service.scraping.ScrapePreviewDataStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("GooglePreviewData")
public class GooglePreviewDataStrategy implements ScrapePreviewDataStrategy {


    private final static Logger logger = LoggerFactory.getLogger(GooglePreviewDataStrategy.class);

    private final static int PAGES_TO_FETCH = 4;

    @Value("${thingiverse.google.searchengine}")
    private String customSearchEngineID;
    @Autowired
    private ThingiverseApiClient thingiverseApiClient;
    @Autowired
    private GoogleApiClient googleApiClient;
    @Autowired
    private LocalFileHostingService fileHoster;


    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm, SearchFilter filter, String websiteName) {
        logger.debug("scraping thingiverse preview data from google");
        GoogleSearchResponse response = null;
        List<ModelPreview> modelPreviews = List.of();
        modelPreviews = new ArrayList<>();
        List<String> ids = List.of();
        for (int page = 0; page < PAGES_TO_FETCH; page++) {
            try {
                response = googleApiClient.searchTerm(customSearchEngineID, searchTerm, page * GoogleApiClient.RESULTS_PER_PAGE);
                ids = response.getLinks().stream().map(link -> link.substring(link.lastIndexOf(":") + 1)).toList();
                for (String id : ids) {
                    try {
                        ThingiverseThing model = thingiverseApiClient.getThing(id);
                        if (model.getError() != null) continue;
                        if (!filter.isValidModel(new ModelPreview()   //this is done on a temporary model to avoid download and rehost
                                .setDescription(model.getDescription())
                                .setLicense(model.getLicense())
                                .setPrice("0")
                        ))continue;
                        modelPreviews.add(new ModelPreview()
                                .setId(id)
                                .setModelName(model.getName())
                                .setWebsiteLink(model.getPublicUrl())
                                .setWebsiteName(websiteName)
                                .setImageLink(
                                        fileHoster.downloadAndRehost(
                                                model.getPreviewImages()
                                                , model.getPreviewImages().substring(model.getPreviewImages().lastIndexOf('.')
                                                )
                                        )
                                )
                                .setPrice("0")
                                .setFeatured(model.isFeatured())
                                .setMakesCount(model.getMakeCount())
                                .setLikesCount(model.getLikeCount())
                                .setCommentsCount(model.getCommentCount())
                                .setFiles(model.getFiles())
                                .setDescription(model.getDescription())
                                .setLicense(model.getLicense())
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
