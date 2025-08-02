package guc.internship.modelscrapper.service.scraping.cults3d;

import guc.internship.modelscrapper.client.cults3d.Cults3DApiClient;
import guc.internship.modelscrapper.client.google.GoogleApiClient;
import guc.internship.modelscrapper.dto.cults3d.Cults3DCreation;
import guc.internship.modelscrapper.dto.cults3d.Cults3DDTO;
import guc.internship.modelscrapper.dto.google.GoogleSearchResponse;
import guc.internship.modelscrapper.dto.search.SearchFilter;
import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.scraping.ScrapePreviewDataStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service("CultsGooglePreview")
public class GooglePreviewDataStrategy implements ScrapePreviewDataStrategy {

    private final static Logger logger = LoggerFactory.getLogger(GooglePreviewDataStrategy.class);

    private final static int PAGES_TO_FETCH = 4;

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
    public List<ModelPreview> scrapePreviewData(String searchTerm, SearchFilter filter, String websiteName) {
        logger.debug("getting cults google preview data");
        List<String> slugs = List.of();
        List<ModelPreview> modelPreviews = new ArrayList<>();
        for (int page = 0; page < PAGES_TO_FETCH; page++){
            try {
                GoogleSearchResponse response = googleApiClient.searchTerm(cx, searchTerm, page * GoogleApiClient.RESULTS_PER_PAGE);
                slugs = response.getLinks().stream().map(l -> l.substring(l.lastIndexOf("/") + 1)).toList();
                modelPreviews.addAll(slugs.stream().map(slug -> {
                            String query = String.format(QUERY, slug).replaceAll("\n", "");
                            Cults3DCreation model = cultsApi.getModel(query);
                            if (model.getData().getCreation() == null) {
                                logger.debug("no cults3d model with slug {}", slug);
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
                                    .setFeatured(dto.isFeatured())
                                    .setDescription(dto.getDescription())
                                    .setLicense(dto.getLicenseName());
                        }
                ).filter(modelPreview -> Objects.nonNull(modelPreview) && filter.isValidModel(modelPreview)).toList());
                logger.debug("retrieved cults3d google models");
            } catch (Exception e) {
                logger.error("couldn't get cults' google preview data {} exception ", e.getMessage());
                logger.error("obtained slugs {}", slugs);
            }
        }
        return modelPreviews;
    }
}
