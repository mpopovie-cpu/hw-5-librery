package ru.otus.hw.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.User;
import ru.otus.hw.repositories.UserRepository;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

@Component
@RequiredArgsConstructor
public class SecurityDataInitializer {
    private static final Logger logger = Logger.getLogger(SecurityDataInitializer.class.getName());
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void init() {
        try {
            logger.info(">>> [DATABASE RESET] Starting hard reset of users collection...");
            
            // Force drop the collection to ensure no stale data
            mongoTemplate.dropCollection("users");
            logger.info(">>> MongoDB 'users' collection dropped.");

            saveUser("admin", "password", List.of("ROLE_ADMIN"));
            saveUser("user", "password", List.of("ROLE_USER"));
            
            logger.info(">>> [SUCCESS] Admin and User accounts created. LOGIN READY: admin/password and user/password");
        } catch (Exception e) {
            logger.log(Level.SEVERE, ">>> [CRITICAL FAILURE] Could not initialize security users!", e);
        }
    }

    private void saveUser(String username, String password, List<String> roles) {
        User user = new User(username, passwordEncoder.encode(password), roles);
        userRepository.save(user);
        logger.info("Successfully saved: " + username);
    }
}
