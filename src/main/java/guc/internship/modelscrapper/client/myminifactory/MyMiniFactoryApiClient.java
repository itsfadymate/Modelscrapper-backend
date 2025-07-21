package guc.internship.modelscrapper.client.myminifactory;

import guc.internship.modelscrapper.dto.myminifactory.MyMiniFactoryDTO;
import guc.internship.modelscrapper.dto.myminifactory.MyMiniFactoryObjectMakes;
import guc.internship.modelscrapper.dto.myminifactory.MyMiniFactorySearchResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="MyMiniFactory-api", url="${MyMiniFactory.api.url}", configuration = MyMiniFactoryFeignConfig.class)
public interface MyMiniFactoryApiClient {

    @GetMapping("/search")
    MyMiniFactorySearchResponse searchMiniFactory(@RequestParam(value = "q") String searchTerm);

    @GetMapping("/objects/{objectId}/prints")
    MyMiniFactoryObjectMakes getObjectPrints(@PathVariable("objectId")String id);

    @GetMapping("/objects/{objectId}")
    MyMiniFactoryDTO getObject(@PathVariable("objectId")String id);
}
