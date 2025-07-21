package guc.internship.modelscrapper.service.scraping.sketchfab;

import guc.internship.modelscrapper.client.google.GoogleApiClient;
import guc.internship.modelscrapper.client.sketchfab.SketchfabApiClient;
import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.scraping.ScrapePreviewDataStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("sketchFabGooglePreviewer")
public class GooglePreviewDataStrategy implements ScrapePreviewDataStrategy {

    private static final Logger logger = LoggerFactory.getLogger(GooglePreviewDataStrategy.class);
    @Autowired
    private SketchfabApiClient sketchFabApiClient;
    @Autowired
    private GoogleApiClient googleApiClient;
    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm, boolean showFreeOnly) {
        return List.of();
    }
}
