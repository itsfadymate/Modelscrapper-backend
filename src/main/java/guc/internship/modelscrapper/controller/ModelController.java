package guc.internship.modelscrapper.controller;

import guc.internship.modelscrapper.model.Model3D;
import guc.internship.modelscrapper.service.ModelScrapingOrchestrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/models")
@CrossOrigin(origins = "*")
public class ModelController {

    @Autowired
    private ModelScrapingOrchestrator scrapingOrchestrator;

    @GetMapping("/search")
    public List<Model3D> searchModels(@RequestParam String searchTerm) {
        return scrapingOrchestrator.scrapeAll(searchTerm);
    }

    @GetMapping("/sources")
    public List<String> getAvailableSources() {
        return scrapingOrchestrator.getAvailableSources();
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "OK", "service", "Model Scrapper API");
    }
}