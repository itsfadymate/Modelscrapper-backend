package guc.internship.modelscrapper.client;

import guc.internship.modelscrapper.dto.ThingiverseSearchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="thingiverse-api", url="${thingiverse.api.url}")
public interface ThingiverseApiClient {

    @GetMapping("/search/{term}")
    ThingiverseSearchResponse searchThings(
            @RequestParam("term") String searchTerm,
            @RequestParam(value = "sort", defaultValue = "relevant") String sortCriteria,
            @RequestParam(value = "has_makes", defaultValue = "1") int hasMakes
    );
}
