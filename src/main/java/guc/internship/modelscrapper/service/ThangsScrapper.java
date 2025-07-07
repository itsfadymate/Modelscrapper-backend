package guc.internship.modelscrapper.service;

import guc.internship.modelscrapper.model.ModelPreview;
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

@Service
public class ThangsScrapper implements PreviewScrapingService{

    @Value("${Thangs.search.url}")
    private String url;

    private static final String QUERY_PARAMETERS="?scope=thangs"+
            "&view=grid"+
            "&fileTypes=stl"
            +"&freeModels=true";

    private static final Logger logger = LoggerFactory.getLogger(ThangsScrapper.class);

    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm,boolean showFreeOnly) {
        url = url+searchTerm+QUERY_PARAMETERS + (showFreeOnly? "" : "&paidModels=true" );
        logger.info("searching for {} in url {}",searchTerm,url);
        try{
            Document doc = Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
            .timeout(10000).get();

            Elements cards = doc.select("[data-testid=search-result-link]");

            return cards.stream().
                    map(this::extractCardData).
                    collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error while scraping Thangs: {}", e.getMessage());
            return List.of();
        }
    }

    private ModelPreview extractCardData(Element card){
        try {
            Element imageElement = card.selectFirst(".swiper-slide img");
            String imageSrc = extractDecodedImageUrl(imageElement);


            Element linkElement = card.getElementsByTag("a").getFirst();
            String modelLink = null;
            if (linkElement != null) {
                String href = linkElement.attr("href");
                modelLink = href.startsWith("/") ? "https://thangs.com" + href : href;
            }

            Element bottomRow = card.selectFirst(".ModelCard_BottomRow-0-2-411 h4");
            if (bottomRow == null) {
                bottomRow = card.selectFirst("h4[class*='ModelCard_ModelName']");
            }
            String modelName = null;
            boolean isPaid = false;
            int likeCount =0;
            if (bottomRow != null) {
                modelName = bottomRow.text().trim();
                isPaid = bottomRow.selectFirst("svg[class*='DownloadButton_Icon']")==null;
                Element likeElement = bottomRow.selectFirst(".vote_score");
                if (likeElement!=null)
                    likeCount = Integer.parseInt(likeElement.text());
            }


            if (modelName != null && !modelName.isEmpty() && modelLink != null) {
                return new ModelPreview()
                        .setImageLink(imageSrc)
                        .setModelName(modelName)
                        .setWebsiteName(this.getSourceName())
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

    @Override
    public String getSourceName() {
        return "Thangs";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
