package guc.internship.modelscrapper.service;

import guc.internship.modelscrapper.model.Model3D;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ModelScrapingOrchestrator {

    @Autowired
    private List<PreviewScrapingService> previewScrapingServices;

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    public List<Model3D> scrapeAll(String searchTerm) {
        List<Model3D> allResults = new ArrayList<>();
        
        List<CompletableFuture<List<Model3D>>> futures = new ArrayList<>();
        
        for (PreviewScrapingService service : previewScrapingServices) {
            if (service.isEnabled()) {
                CompletableFuture<List<Model3D>> future = CompletableFuture.supplyAsync(
                    () -> {
                        try {
                            return service.scrape(searchTerm);
                        } catch (Exception e) {
                            System.err.println("Error scraping from " + service.getSourceName() + ": " + e.getMessage());
                            return new ArrayList<>();
                        }
                    }, executorService
                );
                futures.add(future);
            }
        }
        
       for (CompletableFuture<List<Model3D>> future : futures) {
            try {
                allResults.addAll(future.get());
            } catch (Exception e) {
                System.err.println("Error getting scraping results: " + e.getMessage());
            }
        }
        
        return allResults;
    }


    public List<String> getAvailableSources() {
        List<String> sources = new ArrayList<>();
        for (PreviewScrapingService service : previewScrapingServices) {
            if (service.isEnabled()) {
                sources.add(service.getSourceName());
            }
        }
        return sources;
    }
}