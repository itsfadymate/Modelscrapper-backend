package guc.internship.modelscrapper.service.scraping.sketchfab;

import guc.internship.modelscrapper.client.sketchfab.SketchfabApiClient;
import guc.internship.modelscrapper.dto.sketchfab.SketchfabSearchResponse;
import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.scraping.ScrapingService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import org.slf4j.Logger;


@Service
public class SketchfabScrapper implements ScrapingService {

    @Autowired
    private SketchfabApiClient apiClient;

    private static final Logger logger = LoggerFactory.getLogger(SketchfabScrapper.class);

    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm, boolean showFreeOnly) {
        try{
            SketchfabSearchResponse searchResponse = apiClient.searchSketchfab(searchTerm,showFreeOnly);

            return searchResponse.getResults().stream()
                    .filter(o->!o.isNsfw())
                    .map(dto->new ModelPreview()
                            .setId(dto.getId())
                            .setModelName(dto.getName())
                            .setImageLink(dto.getPreviewImageUrl())
                            .setWebsiteName(this.getSourceName())
                            .setWebsiteLink(dto.getUrl())
                            .setCommentsCount(dto.getCommentCount())
                            .setLikesCount(dto.getLikesCount())
                            .setFeatured(dto.isFeatured())
                            .setPrice(dto.isDownloadable()? "0":"paid")
                    ).toList();
        } catch (Exception e) {
            logger.warn("An error occured while scrapping sketchfab {}",e.getMessage());
            return List.of();
        }
    }

    @Override
    public String getSourceName() {
        return "Sketchfab";
    }

    @Override
    public List<ModelPreview.File> getDownloadLinks(String id, String downloadPageUrl) {
        //TODO: get download links
        return List.of();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
