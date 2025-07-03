package guc.internship.modelscrapper.service;

import guc.internship.modelscrapper.model.Model3D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ModelScrapingOrchestrator {

    private static final Logger logger = LoggerFactory.getLogger(ModelScrapingOrchestrator.class);

    @Autowired
    private List<PreviewScrapingService> previewScrapingServices;

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    public List<Model3D> scrapeAll(String searchTerm) {
        logger.debug("Starting scrape for all services with search term: {}", searchTerm);
        logger.debug("Found {} preview scraping services", previewScrapingServices.size());
        
        List<Model3D> allResults = new ArrayList<>();
        List<CompletableFuture<List<Model3D>>> futures = new ArrayList<>();
        
        for (PreviewScrapingService service : previewScrapingServices) {
            logger.debug("Checking service: {}, enabled: {}", service.getSourceName(), service.isEnabled());
            
            if (service.isEnabled()) {
                logger.debug("Starting async scrape for service: {}", service.getSourceName());
                
                CompletableFuture<List<Model3D>> future = CompletableFuture.supplyAsync(
                    () -> {
                        try {
                            logger.debug("Executing scrape for service: {}", service.getSourceName());
                            List<Model3D> results = service.scrape(searchTerm);
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
        
        for (CompletableFuture<List<Model3D>> future : futures) {
            try {
                List<Model3D> results = future.get();
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
        
        for (PreviewScrapingService service : previewScrapingServices) {
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
}