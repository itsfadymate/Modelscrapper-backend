package guc.internship.modelscrapper.service.scraping.thingiverse;

import guc.internship.modelscrapper.client.thingiverse.ThingiverseApiClient;
import guc.internship.modelscrapper.dto.search.SearchFilter;
import guc.internship.modelscrapper.dto.thingiverse.ThingiverseThing;
import guc.internship.modelscrapper.model.ModelDetails;
import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.scraping.ScrapePreviewDataStrategy;
import guc.internship.modelscrapper.service.scraping.ScrapingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ThingiverseScrapper implements ScrapingService {

    @Autowired
    private ThingiverseApiClient thingiverseApiClient;

    @Autowired
    @Qualifier("GooglePreviewData")
    private ScrapePreviewDataStrategy googleDataScrapper;

    @Autowired
    @Qualifier("ApiPreviewData")
    private ScrapePreviewDataStrategy ApiDataScrapper;

    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm, SearchFilter filter) { //haven't found a paid model here
        return filter.isSourceToGoogle(this.getSourceName())? googleDataScrapper.scrapePreviewData(searchTerm, filter,getSourceName() ) : ApiDataScrapper.scrapePreviewData(searchTerm, filter,getSourceName() );
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
    public ModelDetails getModelDetails(String id, String downloadPageUrl) {
        ThingiverseThing thing = thingiverseApiClient.getThing(id);
        return new ModelDetails(thing.getLicense(),thing.getDescription(),thing.getFiles());
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
