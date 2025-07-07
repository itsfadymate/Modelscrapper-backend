package guc.internship.modelscrapper.service;

import guc.internship.modelscrapper.client.myminifactory.MyMiniFactoryApiClient;
import guc.internship.modelscrapper.dto.myminifactory.MyMiniFactorySearchResponse;
import guc.internship.modelscrapper.model.ModelPreview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MyMiniFactoryScrapper implements PreviewScrapingService{
    private static final Logger logger = LoggerFactory.getLogger(MyMiniFactoryScrapper.class);
    @Autowired
    private MyMiniFactoryApiClient apiClient;

    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm,boolean showFreeOnly) {
        if (showFreeOnly) return List.of(); //I am assuming all are paid
        try {
            logger.debug("Searching MyMiniFactory API for: {}", searchTerm);

            MyMiniFactorySearchResponse response = apiClient.searchMiniFactory(searchTerm);


            List<ModelPreview> previews = response.getItems().stream()
                    .map((dto)->new ModelPreview()
                            .setImageLink(dto.getPreviewImageUrl())
                            .setModelName(dto.getName())
                            .setWebsiteName(this.getSourceName())
                            .setWebsiteLink(dto.getUrl())
                            .setMakesCount(dto.getMakesCount())
                            .setFiles(dto.getFiles())
                            .setLikesCount(dto.getLikesCount())
                            .setPrice("paid")

                    )
                    .collect(Collectors.toList());

            logger.debug("Found {} results from MyMiniFactory API", previews.size());
            return previews;

        } catch (Exception e) {
            logger.error("Error calling MyMiniFactory API for search term: {}", searchTerm, e);
            return List.of();
        }
    }

    @Override
    public String getSourceName() {
        return "MyMiniFactory";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
