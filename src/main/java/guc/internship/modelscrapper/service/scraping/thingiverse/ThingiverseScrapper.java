package guc.internship.modelscrapper.service.scraping.thingiverse;

import guc.internship.modelscrapper.client.thingiverse.ThingiverseApiClient;
import guc.internship.modelscrapper.dto.thingiverse.ThingiverseSearchResponse;
import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.localfilehosting.LocalFileHostingService;
import guc.internship.modelscrapper.service.scraping.ScrapingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ThingiverseScrapper implements ScrapingService {
    
    private static final Logger logger = LoggerFactory.getLogger(ThingiverseScrapper.class);
    private static final int hasMakes =1;
    private static final String type ="thing";
    private static final String sortCriteria = "relevant";
    
    @Autowired
    private ThingiverseApiClient thingiverseApiClient;

    @Autowired
    private LocalFileHostingService fileHoster;

    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm,boolean showFreeOnly) { //haven't found a paid model here
        try {
            logger.debug("Searching Thingiverse API for: {}", searchTerm);
            
            ThingiverseSearchResponse response = thingiverseApiClient.searchThings(searchTerm,type,hasMakes,sortCriteria);
            
            
            List<ModelPreview> previews = response.getHits().stream()
                    .filter(searchObject -> !searchObject.isNsfw())
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
                            .setWebsiteName(this.getSourceName())
                            .setMakesCount(searchObject.getMakeCount())
                            .setPrice("0")
                            .setLikesCount(searchObject.getLikeCount())
                            .setCommentsCount(searchObject.getCommentCount())
                            .setFeatured(searchObject.isFeatured())
                    )
                    .collect(Collectors.toList());
            
            logger.debug("Found {} results from Thingiverse API", previews.size());
            return previews;
            
        } catch (Exception e) {
            logger.error("Error calling Thingiverse API for search term: {}", searchTerm, e);
            return List.of();
        }
    }

    @Override
    public String getSourceName() {
        return "thingiverse";
    }

    @Override
    public List<ModelPreview.File> getDownloadLinks(String id, String downloadPageUrl) {
        return thingiverseApiClient.getThing(id).getFiles();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
