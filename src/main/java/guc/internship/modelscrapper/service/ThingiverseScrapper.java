package guc.internship.modelscrapper.service;

import guc.internship.modelscrapper.client.thingiverse.ThingiverseApiClient;
import guc.internship.modelscrapper.dto.thingiverse.ThingiverseSearchResponse;
import guc.internship.modelscrapper.model.ModelPreview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ThingiverseScrapper implements PreviewScrapingService{
    
    private static final Logger logger = LoggerFactory.getLogger(ThingiverseScrapper.class);
    private static final int hasMakes =1;
    private static final String type ="thing";
    private static final String sortCriteria = "relevant";
    
    @Autowired
    private ThingiverseApiClient thingiverseApiClient;
    
   
    
    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm) {
        try {
            logger.debug("Searching Thingiverse API for: {}", searchTerm);
            
            ThingiverseSearchResponse response = thingiverseApiClient.searchThings(searchTerm,type,hasMakes,sortCriteria);
            
            
            List<ModelPreview> previews = response.getHits().stream()
                .map((thing)->new ModelPreview()
                                .setImageLink(thing.getThumbnail())
                                .setModelName(thing.getName())
                                .setWebsiteName(this.getSourceName())
                                .setWebsiteLink(thing.getPublicUrl())
                                .setMakesCount(thing.getMakeCount())
                                .setPrice(0)
                                .setFiles(thing.getFiles())
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
    public boolean isEnabled() {
        return true;
    }
}
