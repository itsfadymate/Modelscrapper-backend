package guc.internship.modelscrapper.service;

import guc.internship.modelscrapper.model.Model3D;
import java.util.List;

public interface PreviewScrapingService {
    List<Model3D> scrape(String searchTerm);
    String getSourceName();
    boolean isEnabled();
}