package guc.internship.modelscrapper.service.scraping.cults3d;

import guc.internship.modelscrapper.client.cults3d.Cults3DApiClient;
import guc.internship.modelscrapper.client.google.GoogleApiClient;
import guc.internship.modelscrapper.dto.cults3d.Cults3DCreation;
import guc.internship.modelscrapper.dto.cults3d.Cults3DDTO;
import guc.internship.modelscrapper.dto.google.GoogleSearchResponse;
import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.scraping.ScrapePreviewDataStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service("CultsGooglePreview")
public class GooglePreviewDataStrategy implements ScrapePreviewDataStrategy {

    private final static Logger logger = LoggerFactory.getLogger(GooglePreviewDataStrategy.class);

    @Autowired
    private GoogleApiClient googleApiClient;

    @Autowired
    private Cults3DApiClient cultsApi;

    @Value("${cults.google.searchengine}")
    private String cx;

    private final static String QUERY = """
    {"query" : "query Creation {
                creation(slug: \\"%s\\") {
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
                }
            }"
    }
    """;

    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm, boolean showFreeOnly) {
        logger.debug("getting cults google preview data");
        List<String> slugs = List.of();
        List<ModelPreview> modelPreviews = List.of();
        String websiteName = new Cults3DScrapper().getSourceName();
        try{
            GoogleSearchResponse response = googleApiClient.searchTerm(cx,searchTerm);
            slugs = response.getLinks().stream().map(l->l.substring(l.lastIndexOf("/"))).toList();
            modelPreviews = slugs.stream().map(slug->{
                String query = String.format(QUERY,slug).replaceAll("\n","");
                Cults3DCreation model = cultsApi.getModel(query);
                if (model.getData().getCreation()==null){
                    logger.debug("no cults3d model with slug {}",slug);
                    return null;
                }
                Cults3DDTO dto = model.getData().getCreation();
                return new ModelPreview()
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
                        .setFeatured(dto.isFeatured());
             }
            ).filter(Objects::nonNull).toList();
            logger.debug("retrieved cults3d google models");
        }catch(Exception e){
            logger.error("couldn't get cults' google preview data {} ",e.getMessage());
        }
        return modelPreviews;
    }
}
