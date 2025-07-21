package guc.internship.modelscrapper.client.google;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class GoogleFeignConfig {
    @Value("${google.api.key}")
    private String apiKey;

    @Bean
    public RequestInterceptor interceptor(){
        return template -> template.query("key",apiKey);
    }
}
