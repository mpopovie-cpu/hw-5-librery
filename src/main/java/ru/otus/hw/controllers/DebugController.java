package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.repositories.UserRepository;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class DebugController {
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    public DebugController(UserRepository userRepository, MongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping("/debug/users")
    public List<String> getUsers() {
        return userRepository.findAll().stream()
                .map(u -> u.getUsername() + " [" + u.getRoles() + "]")
                .collect(Collectors.toList());
    }

    @GetMapping("/debug/db")
    public String checkDb() {
        try {
            String dbName = mongoTemplate.getDb().getName();
            long count = mongoTemplate.getCollection("users").countDocuments();
            return "Connected to DB: " + dbName + " | Users in collection: " + count;
        } catch (Exception e) {
            return "DB Connection Error: " + e.getMessage();
        }
    }
}
