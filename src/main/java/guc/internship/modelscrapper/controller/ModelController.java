package guc.internship.modelscrapper.controller;

import guc.internship.modelscrapper.model.ModelPreview;
import guc.internship.modelscrapper.service.estimator.EstimatingStrategy;
import guc.internship.modelscrapper.service.localfilehosting.LocalFileHostingService;
import guc.internship.modelscrapper.service.scraping.ModelScrapingOrchestrator;
import guc.internship.modelscrapper.util.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
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

    @Autowired
    private EstimatingStrategy estimator;

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
        results.stream().forEach(resultFile -> {
            if (!resultFile.getName().endsWith(".stl")) return;
            File stlFile = null;
            try {
                 stlFile= FileManager.downloadFile(resultFile.getDownloadUrl(), resultFile.getName());
                resultFile.setVolume(estimator.getVolume(stlFile));
            } catch (IOException e) {
                logger.debug("couldn't download stl File ",e);
            }finally {
                FileManager.deleteFile(stlFile);
            }

        });
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