package guc.internship.modelscrapper.service;

import guc.internship.modelscrapper.model.Model3D;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class ThingiversePreviewPreviewScrapingService implements PreviewScrapingService {

    @Override
    public List<Model3D> scrape(String searchTerm) {
        List<Model3D> results = new ArrayList<>();
        
        try {
            String url = "https://www.thingiverse.com/search?q=" + searchTerm.replace(" ", "+") ;
            Element nextPageButton;
            ArrayList<Model3D> models = new ArrayList<>();
            do{
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .get();

                models.addAll(documentScrape(doc));
                nextPageButton = doc.selectFirst("[aria-label='Next page']");
                url = nextPageButton==null?"": nextPageButton.absUrl("href");
            }while(nextPageButton!=null && !nextPageButton.hasAttr("disabled"));

            
            return models;
        } catch (IOException e) {
            System.err.println("Error scraping Thingiverse: " + e.getMessage());
        }
        
        return results;
    }

    private List<Model3D> documentScrape(Document doc) {
        Elements cards = doc.getElementsByClass("ItemCardContainer__itemCard--GGbYM");
        List<Model3D> models = new ArrayList<>();
        for (Element c : cards){
            try{
                 models.add(cardScrape(c));
            }catch (Exception e){
                System.out.println("couldn't parse a model card");
            }
        }
        return models;
    }
    private Model3D cardScrape(Element card){
        Model3D model = new Model3D();
        Element lowerATag  = card.getElementsByClass("ItemCardHeader__itemCardHeader--cPULo").first();
        model.setModelname(lowerATag.text());
        model.setWebsitelink(lowerATag.absUrl("href"));
        model.setWebsitename(this.getSourceName());
        Elements img = card.getElementsByClass("ItemCardContent__itemCardContentImage--uzD0A");
        model.setImagelink(img.attr("src"));
        return model;
    }

    @Override
    public String getSourceName() {
        return "Thingiverse";
    }


    //set to false when the scrapper inevitably breaks down :)
    @Override
    public boolean isEnabled() {
        return true;
    }
}