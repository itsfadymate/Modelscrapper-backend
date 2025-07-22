package guc.internship.modelscrapper.service.scraping.thangs;

import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.scraping.ScrapePreviewDataStrategy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service("ThangsJsoup")
public class JsoupPreviewDataStrategy implements ScrapePreviewDataStrategy {

    @Value("${Thangs.search.url}")
    private String url;

    private static final String QUERY_PARAMETERS="?scope=thangs"+
            "&view=grid"+
            "&fileTypes=stl"
            +"&freeModels=true";

    private static final Logger logger = LoggerFactory.getLogger(JsoupPreviewDataStrategy.class);

    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm, boolean showFreeOnly, String websiteName) {
        url = url+searchTerm+QUERY_PARAMETERS + (showFreeOnly? "" : "&paidModels=true" );
        logger.info("searching for {} in url {}",searchTerm,url);
        try{
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000).get();

            Elements cards = doc.select("[data-testid=search-result-link]");

            return cards.stream().
                    map(c->extractCardData(c,websiteName)).
                    collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error while scraping Thangs: {}", e.getMessage());
            return List.of();
        }
    }


    private ModelPreview extractCardData(Element card,String websiteName){
        try {
            Element imageElement = card.selectFirst(".swiper-slide img");
            String imageSrc = extractDecodedImageUrl(imageElement);


            Element linkElement = card.getElementsByTag("a").getFirst();
            String modelLink = null;
            if (linkElement != null) {
                String href = linkElement.attr("href");
                modelLink = href.startsWith("/") ? "https://thangs.com" + href : href;
            }

            Element bottomRow = card.selectFirst(".ModelCard_BottomRow-0-2-411");
            Element modelNameElement = bottomRow != null ? bottomRow.selectFirst("h4") : null;
            if (modelNameElement == null) {
                modelNameElement = card.selectFirst("h4[class*='ModelCard_ModelName']");
            }
            String modelName = null;
            boolean isPaid = false;
            String likeCount = "0";

            if (modelNameElement != null) {
                modelName = modelNameElement.text().trim();
            }

            if (bottomRow != null) {
                Element likeElement = bottomRow.selectFirst("span[class*='Vote_Score']");
                if (likeElement != null) {
                    likeCount = likeElement.text();
                }
                Element downloadButton = bottomRow.selectFirst("svg[class*='DownloadButton']");
                isPaid =  downloadButton == null;
            }


            if (modelName != null && !modelName.isEmpty() && modelLink != null) {
                return new ModelPreview()
                        .setId(modelLink)
                        .setImageLink(imageSrc)
                        .setModelName(modelName)
                        .setWebsiteName(websiteName)
                        .setWebsiteLink(modelLink)
                        .setPrice(isPaid? "paid" : "0")
                        .setFiles(List.of(new ModelPreview.File("FileTypes: STL",null)))
                        .setLikesCount(likeCount);
            }

            logger.warn("Failed to extract essential data - name: {}, link: {} ,imageLink {}", modelName, modelLink,imageSrc);
        } catch (Exception e) {
            logger.error("Error extracting card data", e);
        }
        return null;
    }

    public static String extractDecodedImageUrl(Element imgElement) {
        if (imgElement == null) return null;

        String src = imgElement.attr("src");
        if (src == null || !src.contains("url=")) return null;

        try {

            String prefix = "url=";
            int start = src.indexOf(prefix);
            if (start == -1) return null;

            int end = src.indexOf("&", start);
            String encodedUrl = (end != -1)
                    ? src.substring(start + prefix.length(), end)
                    : src.substring(start + prefix.length());

            return URLDecoder.decode(encodedUrl, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
