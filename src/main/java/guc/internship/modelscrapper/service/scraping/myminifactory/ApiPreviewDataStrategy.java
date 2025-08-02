package guc.internship.modelscrapper.service.scraping.myminifactory;

import guc.internship.modelscrapper.client.myminifactory.MyMiniFactoryApiClient;
import guc.internship.modelscrapper.dto.myminifactory.MyMiniFactorySearchResponse;
import guc.internship.modelscrapper.dto.search.SearchFilter;
import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.scraping.ScrapePreviewDataStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("miniFactoryApiPreviewer")
public class ApiPreviewDataStrategy implements ScrapePreviewDataStrategy {

    private static final Logger logger = LoggerFactory.getLogger(ApiPreviewDataStrategy.class);
    @Autowired
    private MyMiniFactoryApiClient apiClient;

    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm, SearchFilter filter, String websiteName) {
        List<ModelPreview> previews = new ArrayList<>();
        try {
            logger.debug("Searching MyMiniFactory API for: {}", searchTerm);

            MyMiniFactorySearchResponse response = apiClient.searchMiniFactory(searchTerm);

            previews = response.getItems().stream()
                    .map((dto)->new ModelPreview()
                            .setId(String.valueOf(dto.getId()))
                            .setImageLink(dto.getPreviewImageUrl())
                            .setModelName(dto.getName())
                            .setWebsiteName(websiteName)
                            .setWebsiteLink(dto.getUrl())
                            .setMakesCount(dto.getMakesCount())
                            .setFiles(dto.getFiles())
                            .setLikesCount(dto.getLikesCount())
                            .setPrice(dto.getPrice())
                            .setDescription(dto.getDescription())
                            .setLicense(dto.getLicense())
                    ).filter(filter::isValidModel)
                    .collect(Collectors.toList());

            logger.debug("Found {} results from MyMiniFactory API", previews.size());
        } catch (Exception e) {
            logger.error("Error calling MyMiniFactory API for search term: {} exception message: {}", searchTerm, e.getMessage());
        }
        return previews;
    }
}
