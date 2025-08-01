package guc.internship.modelscrapper.service.scraping;

import guc.internship.modelscrapper.model.ModelDetails;
import guc.internship.modelscrapper.model.ModelPreview;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ModelScrapingOrchestrator {

    private static final Logger logger = LoggerFactory.getLogger(ModelScrapingOrchestrator.class);

    @Autowired
    private List<ScrapingService> scrapingServices;

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    Map<String, ScrapingService> serviceMap;

    @PostConstruct
    public void init() {
        serviceMap = new HashMap<>();
        for (ScrapingService service : scrapingServices) {
            serviceMap.put(service.getSourceName().toLowerCase(), service);
        }
    }

    public List<ModelPreview> scrapeAll(String searchTerm, List<String> enabledSources,boolean showFreeOnly,List<String> googleSearchSources) {
        logger.debug("Starting scrape for all services with search term: {}", searchTerm);
        logger.debug("Found {} preview scraping services", scrapingServices.size());
        
        if (enabledSources != null && !enabledSources.isEmpty()) {
            logger.debug("Filtering by enabled sources: {}", enabledSources);
        }
        
        List<ModelPreview> allResults = new ArrayList<>();
        List<CompletableFuture<List<ModelPreview>>> futures = new ArrayList<>();

        HashSet<String> enabledSourcesSet = new HashSet<>(enabledSources==null? List.of() : enabledSources);
        HashSet<String> googleSources = new HashSet<>(googleSearchSources==null? List.of() : googleSearchSources);
        
        for (ScrapingService service : scrapingServices) {
            boolean shouldInclude = service.isEnabled() && 
                (enabledSources == null || enabledSources.isEmpty() ||
                        enabledSourcesSet.contains(service.getSourceName().toLowerCase()));
            boolean searchUsingGoogle = googleSources.contains(service.getSourceName().toLowerCase());
            
            logger.debug("Checking service: {}, enabled: {}, should include: {}, should google: {}",
                service.getSourceName(), service.isEnabled(), shouldInclude,searchUsingGoogle);
            
            if (shouldInclude) {
                logger.debug("Starting async scrape for service: {}", service.getSourceName());
                
                CompletableFuture<List<ModelPreview>> future = CompletableFuture.supplyAsync(
                    () -> {
                        try {
                            logger.debug("Executing scrape for service: {}", service.getSourceName());
                            List<ModelPreview> results = service.scrapePreviewData(searchTerm,showFreeOnly,searchUsingGoogle);
                            logger.debug("Service {} returned {} results", service.getSourceName(), results.size());
                            return results;
                        } catch (Exception e) {
                            logger.error("Error scraping from {}: {}", service.getSourceName(), e.getMessage(), e);
                            return new ArrayList<>();
                        }
                    }, executorService
                );
                futures.add(future);
            }
        }
        
        logger.debug("Waiting for {} futures to complete", futures.size());
        
        for (CompletableFuture<List<ModelPreview>> future : futures) {
            try {
                List<ModelPreview> results = future.get();
                logger.debug("Future completed with {} results", results.size());
                allResults.addAll(results);
            } catch (Exception e) {
                logger.error("Error getting scraping results: {}", e.getMessage(), e);
            }
        }
        
        logger.info("Total results from all services: {}", allResults.size());
        return allResults;
    }

    public List<String> getAvailableSources() {
        logger.debug("Getting available sources");
        List<String> sources = new ArrayList<>();
        
        for (ScrapingService service : scrapingServices) {
            if (service.isEnabled()) {
                sources.add(service.getSourceName());
                logger.debug("Added enabled source: {}", service.getSourceName());
            } else {
                logger.debug("Skipped disabled source: {}", service.getSourceName());
            }
        }
        
        logger.debug("Total available sources: {}", sources.size());
        return sources;
    }

    public List<ModelPreview.File> getDownloadLinks(String sourceName, String id, String downloadPageUrl) {
        List<ModelPreview.File> files = serviceMap.get(sourceName).getDownloadLinks(id,downloadPageUrl);
        return files==null? List.of() : files;
    }

    public ModelDetails getModelDetails(String sourceName, String id, String downloadPageUrl) {
        return serviceMap.get(sourceName).getModelDetails(id,downloadPageUrl);
    }
}