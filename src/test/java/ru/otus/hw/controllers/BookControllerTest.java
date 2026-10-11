package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@DisplayName("Контроллер книг")
@WebMvcTest(BookController.class)
@Import(SecurityConfiguration.class)
class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    private final Author author = new Author("a1", "Author_1");

    private final Genre genre = new Genre("g1", "Genre_1");

    private final Book book = new Book("b1", "Title_1", author, genre);

    @Test
    @WithMockUser
    @DisplayName("GET /books - список книг")
    void shouldShowBookList() throws Exception {
        when(bookService.findAll()).thenReturn(List.of(book));

        mvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("books"))
                .andExpect(model().attribute("books", List.of(book)));
    }

    @Test
    @DisplayName("GET /books без аутентификации - редирект на логин")
    void shouldRedirectWhenUnauthorizedToBookList() throws Exception {
        mvc.perform(get("/books"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET / - редирект на список книг для авторизованного пользователя")
    void shouldRedirectToBooksWhenAuthenticatedOnIndex() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));
    }

    @Test
    @DisplayName("GET / без аутентификации - редирект на логин")
    void shouldRedirectWhenUnauthorizedToIndex() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /books/new - форма создания")
    void shouldShowCreateForm() throws Exception {
        when(authorService.findAll()).thenReturn(List.of(author));
        when(genreService.findAll()).thenReturn(List.of(genre));

        mvc.perform(get("/books/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("book-form"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attribute("authors", List.of(author)))
                .andExpect(model().attribute("genres", List.of(genre)));
    }

    @Test
    @DisplayName("GET /books/new без аутентификации - редирект на логин")
    void shouldRedirectWhenUnauthorizedToCreateForm() throws Exception {
        mvc.perform(get("/books/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /books/new для ROLE_USER - доступ запрещён")
    void shouldForbidUserFromCreateForm() throws Exception {
        mvc.perform(get("/books/new"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /books - создание книги и редирект на список")
    void shouldCreateBook() throws Exception {
        mvc.perform(post("/books")
                        .param("title", "New title")
                        .param("authorId", "a1")
                        .param("genreId", "g1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService).insert("New title", "a1", "g1");
    }

    @Test
    @DisplayName("POST /books без аутентификации - редирект на логин")
    void shouldRedirectWhenUnauthorizedToCreateBook() throws Exception {
        mvc.perform(post("/books")
                        .param("title", "New title")
                        .param("authorId", "a1")
                        .param("genreId", "g1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        verify(bookService, never()).insert(anyString(), anyString(), anyString());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /books для ROLE_USER - доступ запрещён")
    void shouldForbidUserFromCreateBook() throws Exception {
        mvc.perform(post("/books")
                        .param("title", "New title")
                        .param("authorId", "a1")
                        .param("genreId", "g1"))
                .andExpect(status().isForbidden());

        verify(bookService, never()).insert(anyString(), anyString(), anyString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /books с пустым названием - возврат на форму, книга не создаётся")
    void shouldNotCreateBookWithBlankTitle() throws Exception {
        mvc.perform(post("/books")
                        .param("title", " ")
                        .param("authorId", "a1")
                        .param("genreId", "g1"))
                .andExpect(status().isOk())
                .andExpect(view().name("book-form"))
                .andExpect(model().attributeExists("error"));

        verify(bookService, never()).insert(anyString(), anyString(), anyString());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /books/{id} - просмотр книги с комментариями")
    void shouldShowBook() throws Exception {
        when(bookService.findByIdWithComments("b1")).thenReturn(Optional.of(book));

        mvc.perform(get("/books/b1"))
                .andExpect(status().isOk())
                .andExpect(view().name("book-view"))
                .andExpect(model().attribute("book", book));
    }

    @Test
    @DisplayName("GET /books/{id} без аутентификации - редирект на логин")
    void shouldRedirectWhenUnauthorizedToBook() throws Exception {
        mvc.perform(get("/books/b1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /books/{id} для несуществующей книги - 404")
    void shouldReturn404WhenBookNotFound() throws Exception {
        when(bookService.findByIdWithComments("missing")).thenReturn(Optional.empty());

        mvc.perform(get("/books/missing"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error-page"))
                .andExpect(model().attributeExists("message"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /books/{id}/edit - форма редактирования")
    void shouldShowEditForm() throws Exception {
        when(bookService.findById("b1")).thenReturn(Optional.of(book));
        when(authorService.findAll()).thenReturn(List.of(author));
        when(genreService.findAll()).thenReturn(List.of(genre));

        mvc.perform(get("/books/b1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("book-form"))
                .andExpect(model().attribute("book", book))
                .andExpect(model().attribute("authors", List.of(author)))
                .andExpect(model().attribute("genres", List.of(genre)));
    }

    @Test
    @DisplayName("GET /books/{id}/edit без аутентификации - редирект на логин")
    void shouldRedirectWhenUnauthorizedToEditForm() throws Exception {
        mvc.perform(get("/books/b1/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /books/{id}/edit для ROLE_USER - доступ запрещён")
    void shouldForbidUserFromEditForm() throws Exception {
        mvc.perform(get("/books/b1/edit"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /books/{id} - обновление книги и редирект на список")
    void shouldUpdateBook() throws Exception {
        mvc.perform(post("/books/b1")
                        .param("title", "Updated")
                        .param("authorId", "a1")
                        .param("genreId", "g1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService).update("b1", "Updated", "a1", "g1");
    }

    @Test
    @DisplayName("POST /books/{id} без аутентификации - редирект на логин")
    void shouldRedirectWhenUnauthorizedToUpdateBook() throws Exception {
        mvc.perform(post("/books/b1")
                        .param("title", "Updated")
                        .param("authorId", "a1")
                        .param("genreId", "g1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        verify(bookService, never()).update(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /books/{id} для ROLE_USER - доступ запрещён")
    void shouldForbidUserFromUpdateBook() throws Exception {
        mvc.perform(post("/books/b1")
                        .param("title", "Updated")
                        .param("authorId", "a1")
                        .param("genreId", "g1"))
                .andExpect(status().isForbidden());

        verify(bookService, never()).update(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /books/{id}/delete - удаление книги и редирект на список")
    void shouldDeleteBook() throws Exception {
        mvc.perform(post("/books/b1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService).deleteById("b1");
    }

    @Test
    @DisplayName("POST /books/{id}/delete без аутентификации - редирект на логин")
    void shouldRedirectWhenUnauthorizedToDeleteBook() throws Exception {
        mvc.perform(post("/books/b1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        verify(bookService, never()).deleteById(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /books/{id}/delete для ROLE_USER - доступ запрещён")
    void shouldForbidUserFromDeleteBook() throws Exception {
        mvc.perform(post("/books/b1/delete"))
                .andExpect(status().isForbidden());

        verify(bookService, never()).deleteById(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /books/{id}/delete не удаляет книгу")
    void shouldNotDeleteBookByGet() throws Exception {
        mvc.perform(get("/books/b1/delete"))
                .andExpect(status().is4xxClientError());

        verify(bookService, never()).deleteById(any());
    }
}