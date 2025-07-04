package guc.internship.modelscrapper.service;

import guc.internship.modelscrapper.model.ModelPreview;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThingiverseScrapper implements PreviewScrapingService{
    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm) {
        return List.of();
    }

    @Override
    public String getSourceName() {
        return "thingiverse";
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
