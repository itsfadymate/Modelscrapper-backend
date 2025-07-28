package guc.internship.modelscrapper.service.scraping.myminifactory;


import guc.internship.modelscrapper.client.myminifactory.MyMiniFactoryApiClient;
import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.scraping.ScrapePreviewDataStrategy;
import guc.internship.modelscrapper.service.scraping.ScrapingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class MyMiniFactoryScrapper implements ScrapingService {

    private static final Logger logger = LoggerFactory.getLogger(MyMiniFactoryScrapper.class);


    @Autowired
    @Qualifier("miniFactoryGooglePreviewer")
    private ScrapePreviewDataStrategy googlePreviewer;

    @Autowired
    @Qualifier("miniFactoryApiPreviewer")
    private ScrapePreviewDataStrategy ApiPreviewer;

    @Autowired
    private MyMiniFactoryApiClient apiClient;

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
        logger.debug("getting download links for {} with ID {} downloadPage {}",this.getSourceName(),id,downloadPageUrl);
        try {
            return apiClient.getObject(id).getFiles();
        }catch (Exception e){
            logger.error("something went wrong while getting download links for myMiniFactory");
        }
        return List.of();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
