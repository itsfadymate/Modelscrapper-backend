package guc.internship.modelscrapper.client;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThingiverseFeignConfig {
    
    @Value("${thingiverse.api.token}")
    private String apiToken;
    
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> template.header("Authorization", "Bearer " + apiToken);
    }
}