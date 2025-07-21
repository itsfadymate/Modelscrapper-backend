package guc.internship.modelscrapper.service.scraping.myminifactory;

import guc.internship.modelscrapper.client.myminifactory.MyMiniFactoryApiClient;
import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.scraping.ScrapePreviewDataStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("miniFactoryGooglePreviewer")
public class GooglePreviewDataStrategy implements ScrapePreviewDataStrategy {

    private static final Logger logger = LoggerFactory.getLogger(GooglePreviewDataStrategy.class);
    @Autowired
    private MyMiniFactoryApiClient apiClient;
    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm, boolean showFreeOnly) {
        return List.of();
    }
}
