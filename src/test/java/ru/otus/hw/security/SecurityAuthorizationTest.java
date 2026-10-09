package ru.otus.hw.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Авторизация по URL и ролям")
@SpringBootTest
@AutoConfigureMockMvc
class SecurityAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private BookRepository bookRepository;

    private String bookId;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();

        var author = authorRepository.save(new Author("Author"));
        var genre = genreRepository.save(new Genre("Genre"));
        bookId = bookRepository.save(new Book("Title", author, genre)).getId();
    }

    @Test
    @DisplayName("ROLE_USER может читать список книг")
    @WithMockUser(roles = "USER")
    void userCanReadBooks() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ROLE_USER не может создавать книги")
    @WithMockUser(roles = "USER")
    void userCannotCreateBook() throws Exception {
        mockMvc.perform(get("/books/new"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ROLE_ADMIN может открыть форму создания книги")
    @WithMockUser(roles = "ADMIN")
    void adminCanOpenCreateForm() throws Exception {
        mockMvc.perform(get("/books/new"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ROLE_USER не может удалять книгу")
    @WithMockUser(roles = "USER")
    void userCannotDeleteBook() throws Exception {
        mockMvc.perform(post("/books/some-id/delete"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ROLE_ADMIN может удалять книгу")
    @WithMockUser(roles = "ADMIN")
    void adminCanDeleteBook() throws Exception {
        mockMvc.perform(post("/books/some-id/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));
    }

    @Test
    @DisplayName("ROLE_USER может читать REST API")
    @WithMockUser(roles = "USER")
    void userCanReadApi() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("ROLE_USER не может создавать книгу через REST API")
    @WithMockUser(roles = "USER")
    void userCannotCreateViaApi() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType("application/json")
                        .content("""
                                {"title": "T", "authorId": "a", "genreId": "g"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ROLE_ADMIN может создавать книгу через REST API")
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateViaApi() throws Exception {
        var author = authorRepository.findAll().get(0);
        var genre = genreRepository.findAll().get(0);

        mockMvc.perform(post("/api/books")
                        .contentType("application/json")
                        .content("""
                                {"title": "New", "authorId": "%s", "genreId": "%s"}
                                """.formatted(author.getId(), genre.getId())))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("ROLE_USER не может изменять книгу через REST API")
    @WithMockUser(roles = "USER")
    void userCannotUpdateViaApi() throws Exception {
        mockMvc.perform(put("/api/books/1")
                        .contentType("application/json")
                        .content("""
                                {"title": "T", "authorId": "a", "genreId": "g"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ROLE_USER не может удалять книгу через REST API")
    @WithMockUser(roles = "USER")
    void userCannotDeleteViaApi() throws Exception {
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ROLE_USER может добавлять комментарий")
    @WithMockUser(roles = "USER")
    void userCanAddComment() throws Exception {
        mockMvc.perform(post("/books/" + bookId + "/comments")
                        .param("text", "Hello"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/" + bookId));
    }
}
