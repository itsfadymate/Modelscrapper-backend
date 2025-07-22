package guc.internship.modelscrapper.service.scraping;

import java.util.List;

import guc.internship.modelscrapper.model.ModelPreview;

public interface ScrapePreviewDataStrategy {
    List<ModelPreview> scrapePreviewData(String searchTerm, boolean showFreeOnly, String websiteName);
}
