package ru.otus.hw.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // Simple custom check: check if the system is "feeling" good
        boolean isHealthy = checkSystemStatus();
        if (isHealthy) {
            return Health.up()
                    .withDetail("Custom Status", "Everything is running smoothly")
                    .withDetail("Version", "1.0.0")
                    .build();
        }
        return Health.down()
                .withDetail("Custom Status", "System is experiencing issues")
                .build();
    }

    private boolean checkSystemStatus() {
        // In a real app, you would check DB connectivity or external API
        return true; 
    }
}
