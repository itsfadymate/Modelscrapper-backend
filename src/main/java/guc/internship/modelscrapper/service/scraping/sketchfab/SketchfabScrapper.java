package guc.internship.modelscrapper.service.scraping.sketchfab;

import guc.internship.modelscrapper.client.sketchfab.SketchfabApiClient;
import guc.internship.modelscrapper.dto.search.SearchFilter;
import guc.internship.modelscrapper.dto.sketchfab.SketchfabSearchObject;
import guc.internship.modelscrapper.model.ModelDetails;
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

    @Autowired
    private SketchfabApiClient apiClient;

    private static final Logger logger = LoggerFactory.getLogger(SketchfabScrapper.class);

    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm, SearchFilter filter) {
        return filter.isSourceToGoogle(this.getSourceName())?googlePreviewer.scrapePreviewData(searchTerm,filter ,getSourceName() ) : ApiPreviewer.scrapePreviewData(searchTerm, filter,getSourceName() );
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
    public ModelDetails getModelDetails(String id, String downloadPageUrl) {
        logger.debug("getting ModelDetails of sketchFab {}", id);
        List<ModelPreview.File> files = getDownloadLinks(id,downloadPageUrl);
        SketchfabSearchObject model= this.apiClient.getModel(id);
        return new ModelDetails(model.getLicense(),model.getDescription(),files);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
