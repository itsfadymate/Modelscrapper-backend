package guc.internship.modelscrapper.service;

import guc.internship.modelscrapper.model.ModelPreview;
import java.util.List;

public interface PreviewScrapingService {
    List<ModelPreview> scrapePreviewData(String searchTerm);
    String getSourceName();
    boolean isEnabled();
}