package guc.internship.modelscrapper.service.scraping.sketchfab;

import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.scraping.ScrapePreviewDataStrategy;
import guc.internship.modelscrapper.service.scraping.ScrapingService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

import org.slf4j.Logger;


@Service
public class SketchfabScrapper implements ScrapingService {

    @Autowired
    @Qualifier("sketchFabGooglePreviewer")
    private ScrapePreviewDataStrategy googlePreviewer;

    @Autowired
    @Qualifier("sketchFabApiPreviewer")
    private ScrapePreviewDataStrategy ApiPreviewer;

    private static final Logger logger = LoggerFactory.getLogger(SketchfabScrapper.class);

    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm, boolean showFreeOnly,boolean useGoogleEngine) {
        return useGoogleEngine?googlePreviewer.scrapePreviewData(searchTerm,showFreeOnly,getSourceName() ) : ApiPreviewer.scrapePreviewData(searchTerm,showFreeOnly,getSourceName() );
    }

    @Override
    public String getSourceName() {
        return "Sketchfab";
    }

    @Override
    public List<ModelPreview.File> getDownloadLinks(String id, String downloadPageUrl) {
        //TODO: get download links
        return List.of();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
