package guc.internship.modelscrapper.client.myminifactory;

import feign.RequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class MyMiniFactoryFeignConfig {

    private static final Logger logger = LoggerFactory.getLogger(MyMiniFactoryFeignConfig.class);

    @Value("${MyMiniFactory.api.username}")
    private String username;

    @Value("${MyMiniFactory.api.client.key}")
    private String apiKey;

    @Bean
    public RequestInterceptor myMiniFactoryRequestInterceptor() {
        logger.debug("MyMiniFactory username: {}", username);
        logger.debug("MyMiniFactory API key configured: {}", apiKey != null && !apiKey.isEmpty());
        
        return template -> {
            template.header("X-API-Key", apiKey);
            template.header("X-Username", username);
            
            template.header("Accept", "application/json");
            template.header("User-Agent", "ModelScrapper/1.0");
            template.header("Connection", "keep-alive");
        };
    }
}
