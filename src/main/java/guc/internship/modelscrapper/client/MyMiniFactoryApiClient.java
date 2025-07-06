package guc.internship.modelscrapper.client;

import guc.internship.modelscrapper.dto.myminifactory.MyMiniFactorySearchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="MyMiniFactory-api", url="${MyMiniFactory.api.url}", configuration = MyMiniFactoryFeignConfig.class)
public interface MyMiniFactoryApiClient {

    @GetMapping("/search")
    MyMiniFactorySearchResponse searchMiniFactory(@RequestParam(value = "q") String searchTerm);
}
