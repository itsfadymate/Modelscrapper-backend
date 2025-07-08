package guc.internship.modelscrapper.service;

import guc.internship.modelscrapper.model.ModelPreview;
import java.util.List;

public interface PreviewScrapingService {
    List<ModelPreview> scrapePreviewData(String searchTerm,boolean showFreeOnly);
    String getSourceName();
    List<ModelPreview.File> getDownloadLinks(String id);
    boolean isEnabled();

}