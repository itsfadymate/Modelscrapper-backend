package guc.internship.modelscrapper.service.scraping;

import guc.internship.modelscrapper.model.ModelPreview;
import java.util.List;

public interface ScrapingService {
    List<ModelPreview> scrapePreviewData(String searchTerm,boolean showFreeOnly);
    String getSourceName();
    List<ModelPreview.File> getDownloadLinks(String id, String downloadPageUrl);
    boolean isEnabled();

}