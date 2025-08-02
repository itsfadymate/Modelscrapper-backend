package guc.internship.modelscrapper.service.scraping.thingiverse;

import guc.internship.modelscrapper.client.thingiverse.ThingiverseApiClient;
import guc.internship.modelscrapper.dto.search.SearchFilter;
import guc.internship.modelscrapper.dto.thingiverse.ThingiverseSearchResponse;
import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.localfilehosting.LocalFileHostingService;
import guc.internship.modelscrapper.service.scraping.ScrapePreviewDataStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("ApiPreviewData")
public class ApiPreviewDataStrategy implements ScrapePreviewDataStrategy {
    private static final Logger logger = LoggerFactory.getLogger(ApiPreviewDataStrategy.class);
    private static final int hasMakes =1;
    private static final String type ="thing";
    private static final String sortCriteria = "relevant";

    @Autowired
    private ThingiverseApiClient thingiverseApiClient;

    @Autowired
    private LocalFileHostingService fileHoster;


    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm, SearchFilter filter, String websiteName) {
        try {
            logger.debug("Searching Thingiverse API for: {}", searchTerm);

            ThingiverseSearchResponse response = thingiverseApiClient.searchThings(searchTerm,type,hasMakes,sortCriteria);

             List<ModelPreview> previews = response.getHits().stream()
                    .filter(searchObject ->
                            !searchObject.isNsfw() &&
                            filter.isValidModel(new ModelPreview() //this is done on a temporary model to avoid download and rehost
                                    .setDescription(searchObject.getDescription())
                                    .setLicense(searchObject.getLicense())
                                    .setPrice("0")
                            )
                    )
                    .map((searchObject)-> new ModelPreview()
                            .setId(searchObject.getId())
                            .setModelName(searchObject.getName())
                            .setImageLink(
                                    fileHoster.downloadAndRehost(
                                            searchObject.getPreviewImage()
                                            ,searchObject.getPreviewImage().substring(searchObject.getPreviewImage().lastIndexOf('.')
                                            )
                                    )
                            )
                            .setWebsiteLink(searchObject.getPublicUrl())
                            .setWebsiteName(websiteName)
                            .setMakesCount(searchObject.getMakeCount())
                            .setPrice("0")
                            .setLikesCount(searchObject.getLikeCount())
                            .setCommentsCount(searchObject.getCommentCount())
                            .setFeatured(searchObject.isFeatured())
                            .setDescription(searchObject.getDescription())
                            .setLicense(searchObject.getLicense())
                    )
                    .collect(Collectors.toList());

            logger.debug("Found {} results from Thingiverse API", previews.size());
            return previews;

        } catch (Exception e) {
            logger.error("Error calling Thingiverse API for search term: {}", searchTerm, e);
            return List.of();
        }
    }
}
