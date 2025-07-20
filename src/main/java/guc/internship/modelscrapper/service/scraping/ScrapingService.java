package guc.internship.modelscrapper.service.scraping;

import guc.internship.modelscrapper.model.ModelPreview;
import java.util.List;

public interface ScrapingService extends ScrapePreviewData{
    String getSourceName();
    List<ModelPreview.File> getDownloadLinks(String id, String downloadPageUrl);
    boolean isEnabled();

}