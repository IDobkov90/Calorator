package com.example.calorator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${external.api.base-url}")
    private String baseUrl;

    @Bean
    public RestClient nutritionInfoRestClient() {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
