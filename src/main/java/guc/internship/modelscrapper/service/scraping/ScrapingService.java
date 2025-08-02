package guc.internship.modelscrapper.service.scraping;

import guc.internship.modelscrapper.dto.search.SearchFilter;
import guc.internship.modelscrapper.model.ModelDetails;
import guc.internship.modelscrapper.model.ModelPreview;
import java.util.List;

public interface ScrapingService {
    List<ModelPreview> scrapePreviewData(String searchTerm, SearchFilter filter);
    String getSourceName();
    List<ModelPreview.File> getDownloadLinks(String id, String downloadPageUrl);
    ModelDetails getModelDetails(String id,String downloadPageUrl);
    boolean isEnabled();

}