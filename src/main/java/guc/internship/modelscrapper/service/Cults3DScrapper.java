package guc.internship.modelscrapper.service;

import guc.internship.modelscrapper.client.cults3d.Cults3DApiClient;
import guc.internship.modelscrapper.dto.cults3d.Cults3DDTO;
import guc.internship.modelscrapper.dto.cults3d.Cults3DSearchResponse;
import guc.internship.modelscrapper.model.ModelPreview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

//The website API doesn't provide download APIs for "legal reasons"
@Service
public class Cults3DScrapper implements PreviewScrapingService {

    private static final Logger logger = LoggerFactory.getLogger(Cults3DScrapper.class);

    @Autowired
    private Cults3DApiClient apiClient;

    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm) {
        logger.debug("Searching Cults3D API for: {}", searchTerm);

        String query = String.format("""
                {"query": "query Search {
                            search(query: \\"%s\\", onlySafe: true) {
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
            

            List<ModelPreview> previews = response.stream().map(dto ->
                 new ModelPreview()
                         .setImageLink(dto.getIllustrationImageUrl())
                         .setModelName(dto.getName())
                         .setWebsiteName(this.getSourceName())
                         .setWebsiteLink(dto.getUrl())
                         .setPrice(dto.getFormattedPrice())
                         .setFiles(dto.getFiles())
                         .setMakesCount(dto.getMakeCount())
            ).collect(Collectors.toList());
            
            logger.debug("Found {} results from Cults3D API", previews.size());
            return previews;
        } catch (Exception e) {
            logger.error("Error calling Cults API for search term: {}", searchTerm, e);
            return List.of();
        }
    }



    @Override
    public String getSourceName() {
        return "Cults3D";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
