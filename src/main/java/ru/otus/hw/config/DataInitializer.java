package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

// Стартовая "миграция": block() здесь допустим по условиям задания.
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataInitializer implements CommandLineRunner {

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    @Override
    public void run(String... args) {
        seedAuthors().then(seedGenres()).block();
    }

    private Mono<Void> seedAuthors() {
        return authorRepository.count()
                .filter(count -> count == 0)
                .flatMapMany(count -> authorRepository.saveAll(List.of(
                        new Author("Alexandre Dumas"),
                        new Author("Leo Tolstoy"),
                        new Author("Fyodor Dostoevsky"))))
                .then();
    }

    private Mono<Void> seedGenres() {
        return genreRepository.count()
                .filter(count -> count == 0)
                .flatMapMany(count -> genreRepository.saveAll(List.of(
                        new Genre("Adventure"),
                        new Genre("Classic"),
                        new Genre("Drama"))))
                .then();
    }
}