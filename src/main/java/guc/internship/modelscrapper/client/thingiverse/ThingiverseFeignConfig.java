package guc.internship.modelscrapper.client.thingiverse;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;


public class ThingiverseFeignConfig {
    
    @Value("${thingiverse.api.token}")
    private String apiToken;
    
    @Bean
    public RequestInterceptor thingiverseRequestInterceptor() {
        return template -> template.header("Authorization", "Bearer " + apiToken);
    }
}