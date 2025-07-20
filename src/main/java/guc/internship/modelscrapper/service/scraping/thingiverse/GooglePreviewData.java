package guc.internship.modelscrapper.service.scraping.thingiverse;

import guc.internship.modelscrapper.client.google.GoogleApiClient;
import guc.internship.modelscrapper.client.thingiverse.ThingiverseApiClient;
import guc.internship.modelscrapper.dto.google.GoogleSearchResponse;
import guc.internship.modelscrapper.dto.thingiverse.ThingiverseThing;
import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.localfilehosting.LocalFileHostingService;
import guc.internship.modelscrapper.service.scraping.ScrapePreviewData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("GooglePreviewData")
public class GooglePreviewData implements ScrapePreviewData {


    private final static Logger logger = LoggerFactory.getLogger(GooglePreviewData.class);
    @Value("${google.api.key}")
    private String apiKey;
    @Value("${google.custom.searchengine}")
    private String customSearchEngineID;
    @Autowired
    private ThingiverseApiClient thingiverseApiClient;
    @Autowired
    private GoogleApiClient googleApiClient;
    @Autowired
    private LocalFileHostingService fileHoster;


    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm, boolean showFreeOnly) {
          try{
              logger.debug("scraping thingiverse preview data from google");
              GoogleSearchResponse response = googleApiClient.searchTerm(apiKey,customSearchEngineID,searchTerm);
              List<String> ids = response.getIds();
              List<ModelPreview> modelPreviews = new ArrayList<>();
              String websiteName = new ThingiverseScrapper().getSourceName();
              for (String id : ids){
                  ThingiverseThing model = thingiverseApiClient.getThing(id);
                  modelPreviews.add(new ModelPreview()
                          .setId(model.getPublicUrl())
                          .setModelName(model.getName())
                          .setWebsiteLink(model.getPublicUrl())
                          .setWebsiteName(websiteName)
                          .setImageLink(
                                  fileHoster.downloadAndRehost(
                                          model.getPreviewImage()
                                          ,model.getPreviewImage().substring(model.getPreviewImage().lastIndexOf('.')
                                          )
                                  )
                          )
                          .setPrice("0")
                          .setFeatured(model.isFeatured())
                          .setMakesCount(model.getMakeCount())
                          .setLikesCount(model.getLikeCount())
                          .setCommentsCount(model.getCommentCount())
                          .setFiles(model.getFiles())
                  );
              }
              return modelPreviews;
          }catch (Exception e){
              logger.debug("something went wrong while getting previewData from google");
              return List.of();
          }
    }
}
