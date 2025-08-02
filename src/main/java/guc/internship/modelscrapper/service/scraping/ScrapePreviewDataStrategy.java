package guc.internship.modelscrapper.service.scraping;

import java.util.List;

import guc.internship.modelscrapper.dto.search.SearchFilter;
import guc.internship.modelscrapper.model.ModelPreview;

public interface ScrapePreviewDataStrategy {
    List<ModelPreview> scrapePreviewData(String searchTerm, SearchFilter filter, String websiteName);
}
