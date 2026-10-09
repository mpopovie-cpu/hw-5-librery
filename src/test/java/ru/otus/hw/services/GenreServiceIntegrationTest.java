package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
        genreRepository.deleteAll();
        genreRepository.saveAll(List.of(
                new Genre("Genre_1"),
                new Genre("Genre_2"),
                new Genre("Genre_3")));
    }

    @DisplayName("должен возвращать все жанры")
    @Test
    void shouldReturnAllGenres() {
        var genres = genreService.findAll();
        assertThat(genres).hasSize(3)
                .extracting(Genre::getName)
                .containsExactlyInAnyOrder("Genre_1", "Genre_2", "Genre_3");
    }
}