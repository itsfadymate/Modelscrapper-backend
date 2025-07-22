package guc.internship.modelscrapper.service.scraping.printables;

import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.scraping.ScrapePreviewDataStrategy;

import java.util.List;

public class GooglePreviewDataStrategy implements ScrapePreviewDataStrategy {
    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm, boolean showFreeOnly) {
        return List.of();
    }
}
