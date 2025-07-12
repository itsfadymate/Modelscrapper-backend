package guc.internship.modelscrapper.controller;

import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.localfilehosting.LocalFileHostingService;
import guc.internship.modelscrapper.service.scraping.ModelScrapingOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/models")
@CrossOrigin(origins = "*")
public class ModelController {

    private static final Logger logger = LoggerFactory.getLogger(ModelController.class);

    @Autowired
    private ModelScrapingOrchestrator scrapingOrchestrator;

    @Autowired
    private LocalFileHostingService fileHoster;

    @GetMapping("/search")
    public List<ModelPreview> searchModels(
        @RequestParam String searchTerm,
        @RequestParam(required = false) List<String> sources,
        @RequestParam(required = false, defaultValue = "false") Boolean showFreeOnly) {
        
        logger.debug("Received search request for: {}", searchTerm);
        List<ModelPreview> results = scrapingOrchestrator.scrapeAll(searchTerm,sources,showFreeOnly);
        logger.debug("Found {} models for search term: {}", results.size(), searchTerm);
        return results;
    }
    @GetMapping("/download")
    public List<ModelPreview.File> downloadLinks(@RequestParam String sourceName,
                                                 @RequestParam String id,
                                                 @RequestParam String downloadPageUrl){
        logger.debug("Received request for model {} in {}",id,sourceName);
        List<ModelPreview.File> results = scrapingOrchestrator.getDownloadLinks(sourceName,id,downloadPageUrl );
        logger.debug("found the following download links: {}",results);
        return results;
    }

    @GetMapping("/download/localhostedlinks")
    public List<ModelPreview.File> downloadLocalLinks(@RequestParam String sourceName,
                                                      @RequestParam String id,
                                                      @RequestParam String downloadPageUrl){
        List<ModelPreview.File>  results = downloadLinks(sourceName,id,downloadPageUrl);
        results = results.stream().map(file -> new ModelPreview.File(
                file.getName(),
                fileHoster.downloadAndRehost(file.getDownloadUrl(), file.getName())))
                .toList();
        logger.debug("success creating local Hosted links");
        return results;
    }

    @GetMapping("/sources")
    public List<String> getAvailableSources() {
        logger.debug("Getting available sources");
        return scrapingOrchestrator.getAvailableSources();
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        logger.debug("Health check requested");

        return Map.of("status", "OK", "service", "Model Scrapper API");
    }
}