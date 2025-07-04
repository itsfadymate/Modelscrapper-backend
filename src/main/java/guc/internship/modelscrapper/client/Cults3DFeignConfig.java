package guc.internship.modelscrapper.client;

import feign.RequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;


import java.util.Base64;


public class Cults3DFeignConfig {
    
    @Value("${cults3D.api.username}")
    private String username;
    
    @Value("${cults3D.api.key}")
    private String apiKey;

    private static final Logger logger = LoggerFactory.getLogger(Cults3DFeignConfig.class);

    @Bean
    public RequestInterceptor cults3dRequestInterceptor() {
        return template -> {
            String credentials = username + ":" + apiKey;
            String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
            template.header("Authorization", "Basic " + encodedCredentials);
            template.header("Content-Type", "application/json");
            template.header("Accept", "*/*");
            template.header("User-Agent", "ModelScrapper/1.0");
            template.header("Connection", "keep-alive");
        };
    }
}