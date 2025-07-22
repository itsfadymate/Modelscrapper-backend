package guc.internship.modelscrapper.service.scraping.thangs;

import guc.internship.modelscrapper.client.google.GoogleApiClient;
import guc.internship.modelscrapper.dto.google.GoogleItem;
import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.scraping.ScrapePreviewDataStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("ThangsGoogle")
public class GooglePreviewDataStrategy implements ScrapePreviewDataStrategy {
    @Autowired
    private GoogleApiClient googleApi;

    @Value("${Thangs.google.searchengine}")
    private String cx;

    private static final Logger logger = LoggerFactory.getLogger(GooglePreviewDataStrategy.class);

    private static final int PAGES_TO_SEARCH = 7;

    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm, boolean showFreeOnly, String websiteName) {
        logger.info("getting {} preview data from google", websiteName);
        List<ModelPreview> models = new ArrayList<>();
        for (int page = 0; page < PAGES_TO_SEARCH; page++) {
            List<GoogleItem> items = googleApi.searchTerm(cx, searchTerm, page * GoogleApiClient.RESULTS_PER_PAGE).getItems();
            for (GoogleItem item : items) {
                try {
                    if (!validProduct(item.getLink())) {
                        logger.debug("product link {} is not valid", item.getLink());
                        continue;
                    }
                    String price = figureOutPrice(item.getLink(), item.getTitle());
                    if (showFreeOnly && !price.equals("could be paid")) continue;
                    String modelLink = getProperLink(item.getLink());
                    String id = getIdFromProperLink(modelLink);

                    models.add(new ModelPreview()
                            .setId(id)
                            .setModelName(getModelNameFromLink(modelLink))
                            .setImageLink(item.getImageUrl())
                            .setWebsiteLink(getProperLink(item.getLink()))
                            .setWebsiteName(websiteName)
                            .setPrice(price)
                    );
                } catch (Exception e) {
                    logger.error("something wrong happened while analyzing {}, exception {}", item.getLink(),e.getMessage());
                }
            }
        }
        return models;
    }

    private String getProperLink(String link) {
        if (link.matches(".*/memberships(/[0-9]+)?")) return link.substring(0, link.lastIndexOf("/memberships"));
        if (link.endsWith("/buy")) return link.substring(0, link.lastIndexOf("/buy"));
        return link;
    }

    private boolean validProduct(String link) {
        return link.matches("https://thangs.com/[0-9a-zA-Z\\-%/_]*/3d-model/[0-9a-zA-Z\\-%._]*(/memberships(/[0-9]+)?|/buy)?");
    }


    private String getIdFromProperLink(String properLink) {
        return (properLink.contains("-") ? properLink.substring(properLink.lastIndexOf("-") + 1) : properLink).strip();
    }

    private String getModelNameFromLink(String properLink)  {
        String modelTag = properLink.substring(properLink.lastIndexOf("3d-model/") + "3d-model/".length());
        //example model tag:Xbox%20Series%20X%20Tentacle%20Dock%20-%20Print-in-Place-924308
        modelTag = modelTag.contains("-") ? modelTag.substring(0, modelTag.lastIndexOf("-")) : modelTag;
        return URLDecoder.decode(modelTag, StandardCharsets.UTF_8);
    }

    //alternative but it's better to get it from the link
    private String getModelNameFromTitle(String title) {
        String removedSuffix = title.contains("|") ? title.substring(0, title.indexOf("|")) : title;
        removedSuffix = removedSuffix.contains("...") ? removedSuffix.substring(0, removedSuffix.indexOf("...")) : removedSuffix;
        removedSuffix = removedSuffix.contains(" - ") ? removedSuffix.substring(0, removedSuffix.indexOf(" - ")) : removedSuffix;
        int prefixLen = "Select membership plan including".length();
        return removedSuffix.startsWith("Select membership plan including") ?
                removedSuffix.substring(prefixLen + 1).strip() :
                removedSuffix.strip();
    }

    private String figureOutPrice(String link, String title) {
        Pattern price1 = Pattern.compile("[0-9]+(\\.([0-9]+))?[$€]");
        Pattern price2 = Pattern.compile("([$€])[0-9]+(\\.([0-9]*))?");

        Matcher price1Matcher = price1.matcher(title);
        if (price1Matcher.find())
            return price1Matcher.group();

        Matcher price2Matcher = price2.matcher(title);
        if (price2Matcher.find())
            return price2Matcher.group();
        if (link.endsWith("/buy"))
            return "paid";

        if (link.endsWith("/memberships") || link.endsWith("/membership") || link.matches(".*/memberships(/[0-9]+)?"))
            return "requires membership";


        return "could be paid";
    }
}
