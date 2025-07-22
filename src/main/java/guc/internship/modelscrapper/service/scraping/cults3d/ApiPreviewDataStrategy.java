package guc.internship.modelscrapper.service.scraping.cults3d;

import guc.internship.modelscrapper.client.cults3d.Cults3DApiClient;
import guc.internship.modelscrapper.dto.cults3d.Cults3DDTO;
import guc.internship.modelscrapper.dto.cults3d.Cults3DSearchResponse;
import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.scraping.ScrapePreviewDataStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("CultsApiPreview")
public class ApiPreviewDataStrategy implements ScrapePreviewDataStrategy {

    private static final Logger logger = LoggerFactory.getLogger(ApiPreviewDataStrategy.class);

    @Autowired
    private Cults3DApiClient apiClient;

    private final static String QUERY = """
                {"query": "query Search {
                            search(query: \\"%s\\", onlySafe: true, sort: BY_DOWNLOADS) {
                                name(locale: EN)
                                url(locale: EN)
                                illustrationImageUrl(version: DEFAULT)
                                blueprints {
                                    fileName
                                }
                                description(locale: EN)
                                details(locale: EN)
                                price(currency: EUR) {
                                    formatted(locale: EN)
                                }
                                makes {
                                    id
                                }
                                comments {
                                     text
                                }
                                likesCount
                                featured
                                slug
                            }
                        }"
                }
                """;



    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm, boolean showFreeOnly, String websiteName) {
        logger.debug("Searching Cults3D API for: {}", searchTerm);
        String query = String.format(QUERY,searchTerm).replaceAll("\n","");

        try {
            Cults3DSearchResponse searchResponse = apiClient.searchCults(query);
            List<Cults3DDTO> response = searchResponse.getSearchResults();

            if (response == null) {
                logger.warn("getSearchResults() returned null");
                return List.of();
            }

            List<ModelPreview> previews = response.stream().filter(dto-> {
                if (showFreeOnly){
                    return isFree(dto.getFormattedPrice());
                }
                return true;
            }).map(dto ->
                    new ModelPreview()
                            .setId(dto.getSlug())
                            .setImageLink(dto.getIllustrationImageUrl())
                            .setModelName(dto.getName())
                            .setWebsiteName(websiteName)
                            .setWebsiteLink(dto.getUrl())
                            .setPrice(dto.getFormattedPrice())
                            .setFiles(dto.getFiles())
                            .setMakesCount(dto.getMakeCount())
                            .setLikesCount(dto.getLikesCount())
                            .setCommentsCount(String.valueOf(dto.getCommentCount()))
                            .setFeatured(dto.isFeatured())
            ).collect(Collectors.toList());

            logger.debug("Found {} results from Cults3D API", previews.size());
            return previews;
        } catch (Exception e) {
            logger.error("Error calling Cults API for search term: {}", searchTerm, e);
            logger.error("used query {}",query);
            return List.of();
        }
    }
    private boolean isFree(String formattedPrice){
        if (formattedPrice == null || formattedPrice.trim().isEmpty()) {
            return false;
        }
        try {
            String numericPrice = formattedPrice.replaceAll("[^0-9.]", "");
            double price = Double.parseDouble(numericPrice);
            return price == 0;
        } catch (NumberFormatException e) {
            logger.warn("Could not parse price: {}", formattedPrice);
            return false;
        }
    }
}
