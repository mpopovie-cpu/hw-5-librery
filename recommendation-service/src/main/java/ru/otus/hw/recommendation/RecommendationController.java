package ru.otus.hw.recommendation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@Slf4j
@RestController
public class RecommendationController {

    private final Random random = new Random();

    @GetMapping("/recommend/{bookId}")
    public String getRecommendation(@PathVariable String bookId) {
        log.info("Received recommendation request for book: {}", bookId);
        
        // Simulate occasional failure for demonstration purposes
        if (random.nextInt(10) == 0) {
            log.error("Simulating a random internal server error");
            throw new RuntimeException("Internal Server Error simulated");
        }

        String[] recommendations = {
            "If you liked this, try 'The Count of Monte Cristo'",
            "We recommend 'Anna Karenina'",
            "You might also enjoy 'The Brothers Karamazov'",
            "Check out 'Crime and Punishment'"
        };
        return recommendations[random.nextInt(recommendations.length)];
    }
}
