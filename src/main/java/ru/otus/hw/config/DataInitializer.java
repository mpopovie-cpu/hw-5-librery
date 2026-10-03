package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.GenreRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;

    @Override
    public void run(String... args) {
        if (authorRepository.count() == 0) {
            authorRepository.save(new Author("Alexandre Dumas"));
            authorRepository.save(new Author("Leo Tolstoy"));
            authorRepository.save(new Author("Fyodor Dostoevsky"));
        }

        if (genreRepository.count() == 0) {
            genreRepository.save(new Genre("Adventure"));
            genreRepository.save(new Genre("Classic"));
            genreRepository.save(new Genre("Drama"));
        }
    }
}