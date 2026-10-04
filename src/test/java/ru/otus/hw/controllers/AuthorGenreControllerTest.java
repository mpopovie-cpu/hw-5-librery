package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.GenreService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("Контроллеры авторов и жанров")
@WebFluxTest({AuthorController.class, GenreController.class})
class AuthorGenreControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    @Test
    @DisplayName("GET /authors - список авторов")
    void shouldShowAuthors() {
        when(authorService.findAll()).thenReturn(Flux.just(new Author("a1", "Author_1")));

        webTestClient.get().uri("/authors")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Author_1"));
    }

    @Test
    @DisplayName("GET /genres - список жанров")
    void shouldShowGenres() {
        when(genreService.findAll()).thenReturn(Flux.just(new Genre("g1", "Genre_1")));

        webTestClient.get().uri("/genres")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Genre_1"));
    }
}