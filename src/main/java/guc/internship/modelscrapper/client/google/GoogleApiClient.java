package guc.internship.modelscrapper.client.google;

import guc.internship.modelscrapper.dto.google.GoogleSearchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="google-api", url="${google.api.url}")
public interface GoogleApiClient {
  @GetMapping("customsearch/v1")
  GoogleSearchResponse searchTerm(@RequestParam("cx") String searchEngine,
                                  @RequestParam("q") String searchTerm);
}
