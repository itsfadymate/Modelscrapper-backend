package guc.internship.modelscrapper.service.scraping.sketchfab;

import guc.internship.modelscrapper.client.sketchfab.SketchfabApiClient;
import guc.internship.modelscrapper.dto.search.SearchFilter;
import guc.internship.modelscrapper.dto.sketchfab.SketchfabSearchResponse;
import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.scraping.ScrapePreviewDataStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;


@Service("sketchFabApiPreviewer")
public class ApiPreviewDataStrategy implements ScrapePreviewDataStrategy {

    private static final Logger logger = LoggerFactory.getLogger(ApiPreviewDataStrategy.class);
    @Autowired
    private SketchfabApiClient apiClient;

    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm, SearchFilter filter, String websiteName) {
        logger.debug("getting sketchfab api previewData");
        try {
            SketchfabSearchResponse searchResponse = apiClient.searchSketchfab(searchTerm, filter.getShowFreeOnly());

            return searchResponse.getResults().stream()
                    .filter(o ->!o.isNsfw() && o.isDownloadable())
                    .map(dto -> new ModelPreview()
                            .setId(dto.getId())
                            .setModelName(dto.getName())
                            .setImageLink(dto.getPreviewImageUrl())
                            .setWebsiteName(websiteName)
                            .setWebsiteLink(dto.getUrl())
                            .setCommentsCount(dto.getCommentCount())
                            .setLikesCount(dto.getLikesCount())
                            .setFeatured(dto.isFeatured())
                            .setPrice(dto.getPrice()==null? "0" : dto.getPrice())
                            .setEmbeddedViewerUrl(dto.getEmbedUrl())
                            .setDescription(dto.getDescription())
                            .setLicense(dto.getLicense())
                    ).filter(filter::isValidModel).toList();
        } catch (Exception e) {
            logger.warn("An error occurred while scrapping sketchfab {}", e.getMessage());
            return List.of();

        }
    }
}
