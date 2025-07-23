package guc.internship.modelscrapper.client.sketchfab;

import guc.internship.modelscrapper.dto.sketchfab.SketchfabSearchObject;
import guc.internship.modelscrapper.dto.sketchfab.SketchfabSearchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="sketchfab-api", url="${Sketchfab.api.url}")
public interface SketchfabApiClient {

    @GetMapping("/search?type=models")
    SketchfabSearchResponse searchSketchfab(@RequestParam("q") String searchTerm,
                                            @RequestParam("downloadable") boolean downloadable);

    @GetMapping("/models/{uid}")
    SketchfabSearchObject getModel(@PathVariable("uid") String uid);
}
