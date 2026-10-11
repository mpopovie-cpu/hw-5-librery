package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotationTConfiguration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient() {
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory();
        factory.setReadTimeout(Duration.ofSeconds(2));
        
        return RestClient.builder()
                .requestFactory(factory)
                .build();
    }
}
