package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.BookService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("REST-контроллер книг")
@WebFluxTest(BookRestController.class)
class BookRestControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BookService bookService;

    private Book book(String title) {
        return new Book("1", title, new Author("a1", "Author_1"), new Genre("g1", "Genre_1"));
    }

    @Test
    @DisplayName("GET /api/books - список книг")
    void shouldFindAllBooks() {
        when(bookService.findAll()).thenReturn(Flux.just(book("Test")));

        webTestClient.get().uri("/api/books")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("1")
                .jsonPath("$[0].title").isEqualTo("Test")
                .jsonPath("$[0].authorName").isEqualTo("Author_1")
                .jsonPath("$[0].genreName").isEqualTo("Genre_1");
    }

    @Test
    @DisplayName("GET /api/books/{id} - книга по id")
    void shouldFindBookById() {
        when(bookService.findById("1")).thenReturn(Mono.just(book("Test")));

        webTestClient.get().uri("/api/books/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Test")
                .jsonPath("$.authorId").isEqualTo("a1");
    }

    @Test
    @DisplayName("GET /api/books/{id} для несуществующей книги - 404")
    void shouldReturnNotFoundWhenBookDoesNotExist() {
        when(bookService.findById("1")).thenReturn(Mono.empty());

        webTestClient.get().uri("/api/books/1")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Book not found: 1");
    }

    @Test
    @DisplayName("POST /api/books - создание книги")
    void shouldCreateBook() {
        when(bookService.insert("Test", "a1", "g1")).thenReturn(Mono.just(book("Test")));

        webTestClient.post().uri("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new BookRequestDto("Test", "a1", "g1"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.title").isEqualTo("Test");
    }

    @Test
    @DisplayName("POST /api/books с несуществующим автором - 404")
    void shouldReturnNotFoundWhenAuthorDoesNotExist() {
        when(bookService.insert("Test", "bad", "g1"))
                .thenReturn(Mono.error(new EntityNotFoundException("Author not found: bad")));

        webTestClient.post().uri("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new BookRequestDto("Test", "bad", "g1"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Author not found: bad");
    }

    @Test
    @DisplayName("PUT /api/books/{id} - обновление книги")
    void shouldUpdateBook() {
        when(bookService.update("1", "Updated", "a1", "g1")).thenReturn(Mono.just(book("Updated")));

        webTestClient.put().uri("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new BookRequestDto("Updated", "a1", "g1"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Updated");
    }

    @Test
    @DisplayName("DELETE /api/books/{id} - удаление книги")
    void shouldDeleteBook() {
        when(bookService.deleteById("1")).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/books/1")
                .exchange()
                .expectStatus().isNoContent();

        verify(bookService).deleteById("1");
    }
}