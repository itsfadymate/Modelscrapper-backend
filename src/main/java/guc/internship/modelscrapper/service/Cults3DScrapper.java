package guc.internship.modelscrapper.service;

import guc.internship.modelscrapper.client.cults3d.Cults3DApiClient;
import guc.internship.modelscrapper.dto.cults3d.Cults3DDTO;
import guc.internship.modelscrapper.dto.cults3d.Cults3DSearchResponse;
import guc.internship.modelscrapper.dto.cults3d.Cults3DUrlResponse;
import guc.internship.modelscrapper.model.ModelPreview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

//The website API doesn't provide download APIs for "legal reasons"
@Service
public class Cults3DScrapper implements ScrapingService {

    private static final Logger logger = LoggerFactory.getLogger(Cults3DScrapper.class);

    @Autowired
    private Cults3DApiClient apiClient;

    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm,boolean showFreeOnly) {
        logger.debug("Searching Cults3D API for: {}", searchTerm);

        String query = String.format("""
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
                """,searchTerm).replaceAll("\n","");



        //logger.debug("Cults3D GraphQL query: "+ query);
        
        try {

            Cults3DSearchResponse searchResponse = apiClient.searchCults(query);
            //logger.debug("Raw API response: {}", searchResponse);

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
                         .setWebsiteName(this.getSourceName())
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



    @Override
    public String getSourceName() {
        return "Cults3D";
    }

    @Override
    public List<ModelPreview.File> getDownloadLinks(String id) {
        String query = String.format("""
                {"query": "query Creation {
                               creation(slug: \\"%s\\") {
                                   url
                               }
                           }"
                }
                """,id).replaceAll("\n","");
        Cults3DUrlResponse urlResponse = apiClient.getUrl(query);

        //TODO: use playwright to try and fetch the download links
        return List.of();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
