package guc.internship.modelscrapper.client.cults3d;
import guc.internship.modelscrapper.dto.cults3d.Cults3DSearchResponse;
import guc.internship.modelscrapper.dto.cults3d.Cults3DUrlResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@FeignClient(name = "cults3d-api", url = "${cults3D.api.url}", configuration = Cults3DFeignConfig.class)
public interface Cults3DApiClient {
    
    @PostMapping("/graphql")
    Cults3DSearchResponse searchCults(@RequestBody String query);

    @PostMapping("/graphql")
    Cults3DUrlResponse getUrl(@RequestBody String urlQuery);
}
