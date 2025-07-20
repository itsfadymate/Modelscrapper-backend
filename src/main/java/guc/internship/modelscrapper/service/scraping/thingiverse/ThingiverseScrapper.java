package guc.internship.modelscrapper.service.scraping.thingiverse;

import guc.internship.modelscrapper.client.thingiverse.ThingiverseApiClient;
import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.scraping.ScrapePreviewData;
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
    private ScrapePreviewData previewDataScrapper;

    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm,boolean showFreeOnly) { //haven't found a paid model here
        return previewDataScrapper.scrapePreviewData(searchTerm,showFreeOnly);
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
