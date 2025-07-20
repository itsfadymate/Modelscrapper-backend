package guc.internship.modelscrapper.client.thingiverse;

import guc.internship.modelscrapper.dto.thingiverse.ThingiverseSearchResponse;
import guc.internship.modelscrapper.dto.thingiverse.ThingiverseThing;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="thingiverse-api", url="${thingiverse.api.url}", configuration = ThingiverseFeignConfig.class)
public interface ThingiverseApiClient {

    @GetMapping("/search/{term}/")
    ThingiverseSearchResponse searchThings(
            @PathVariable("term") String searchTerm,
            @RequestParam(value = "type", defaultValue = "thing") String type,
            @RequestParam(value = "has_makes", defaultValue = "1") int hasMakes,
            @RequestParam(value = "sort", defaultValue = "relevant") String sortCriteria
    );

    @GetMapping("things/{id}")
    ThingiverseThing getThing(@PathVariable("id") String id);
}
