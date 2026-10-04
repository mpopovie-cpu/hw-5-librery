package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Интеграционный тест GenreService")
@SpringBootTest
class GenreServiceIntegrationTest {

    @Autowired
    private GenreService genreService;

    @Autowired
    private GenreRepository genreRepository;

    @BeforeEach
    void setUp() {
        genreRepository.deleteAll()
                .thenMany(genreRepository.saveAll(List.of(
                        new Genre("Genre_1"), new Genre("Genre_2"), new Genre("Genre_3"))))
                .blockLast();
    }

    @DisplayName("должен возвращать все жанры")
    @Test
    void shouldReturnAllGenres() {
        StepVerifier.create(genreService.findAll().collectList())
                .assertNext(genres -> assertThat(genres)
                        .extracting(Genre::getName)
                        .containsExactlyInAnyOrder("Genre_1", "Genre_2", "Genre_3"))
                .verifyComplete();
    }
}