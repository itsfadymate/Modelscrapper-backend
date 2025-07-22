package guc.internship.modelscrapper.service.scraping.myminifactory;


import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.scraping.ScrapePreviewDataStrategy;
import guc.internship.modelscrapper.service.scraping.ScrapingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class MyMiniFactoryScrapper implements ScrapingService {

    @Autowired
    @Qualifier("miniFactoryGooglePreviewer")
    private ScrapePreviewDataStrategy googlePreviewer;

    @Autowired
    @Qualifier("miniFactoryApiPreviewer")
    private ScrapePreviewDataStrategy ApiPreviewer;

    @Override
    public List<ModelPreview> scrapePreviewData(String searchTerm,boolean showFreeOnly,boolean useGoogleEngine) {
        return useGoogleEngine? googlePreviewer.scrapePreviewData(searchTerm,showFreeOnly,getSourceName() ) : ApiPreviewer.scrapePreviewData(searchTerm,showFreeOnly,getSourceName() );
    }

    @Override
    public String getSourceName() {
        return "MyMiniFactory";
    }

    @Override
    public List<ModelPreview.File> getDownloadLinks(String id, String downloadPageUrl) {
        //TODO: something along the lines of getting the download links yada-yada
        return List.of();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
