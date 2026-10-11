package ru.otus.hw.clients;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationClient {

    private final RestClient restClient;

    @Value("${services.recommendation.url}")
    private String recommendationUrl;

    @CircuitBreaker(name = "recommendationService", fallbackMethod = "getFallbackRecommendation")
    public String getRecommendation(String bookId) {
        log.info("Calling external recommendation service for book: {}", bookId);
        return restClient.get()
                .uri(recommendationUrl + "/recommend/" + bookId)
                .retrieve()
                .body(String.class);
    }

    public String getFallbackRecommendation(String bookId, Throwable t) {
        log.warn("Fallback triggered for book {} due to: {}", bookId, t.getMessage());
        return "Recommendation service is currently unavailable. Please try again later.";
    }
}
