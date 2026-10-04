package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookComment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DisplayName("Контроллер страниц книг")
@WebFluxTest(BookController.class)
class BookControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    private final Author author = new Author("a1", "Author_1");

    private final Genre genre = new Genre("g1", "Genre_1");

    private Book book() {
        return new Book("b1", "Title_1", author, genre);
    }

    @Test
    @DisplayName("GET / - редирект на список книг")
    void shouldRedirectFromRoot() {
        webTestClient.get().uri("/")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/books");
    }

    @Test
    @DisplayName("GET /books - страница списка книг")
    void shouldShowBookList() {
        webTestClient.get().uri("/books")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Books"));
    }

    @Test
    @DisplayName("GET /books/new - форма создания")
    void shouldShowCreateForm() {
        when(authorService.findAll()).thenReturn(Flux.just(author));
        when(genreService.findAll()).thenReturn(Flux.just(genre));

        webTestClient.get().uri("/books/new")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Add Book", "Author_1", "Genre_1"));
    }

    @Test
    @DisplayName("GET /books/{id} - просмотр книги с комментариями")
    void shouldShowBook() {
        var book = book();
        book.setComments(List.of(new BookComment("c1", "Great book", "b1")));
        when(bookService.findByIdWithComments("b1")).thenReturn(Mono.just(book));

        webTestClient.get().uri("/books/b1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Title_1", "Author_1", "Genre_1", "Great book"));
    }

    @Test
    @DisplayName("GET /books/{id} для несуществующей книги - 404")
    void shouldReturn404WhenBookNotFound() {
        when(bookService.findByIdWithComments("missing")).thenReturn(Mono.empty());

        webTestClient.get().uri("/books/missing")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Book not found: missing"));
    }

    @Test
    @DisplayName("GET /books/{id}/edit - форма редактирования")
    void shouldShowEditForm() {
        when(bookService.findById("b1")).thenReturn(Mono.just(book()));
        when(authorService.findAll()).thenReturn(Flux.just(author));
        when(genreService.findAll()).thenReturn(Flux.just(genre));

        webTestClient.get().uri("/books/b1/edit")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("Edit Book", "Title_1", "Author_1", "Genre_1"));
    }

    @Test
    @DisplayName("GET /books/{id}/edit для несуществующей книги - 404")
    void shouldReturn404OnEditWhenBookNotFound() {
        when(bookService.findById("missing")).thenReturn(Mono.empty());

        webTestClient.get().uri("/books/missing/edit")
                .exchange()
                .expectStatus().isNotFound();
    }
}