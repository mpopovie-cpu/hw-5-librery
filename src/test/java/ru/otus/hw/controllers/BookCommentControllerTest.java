package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.repositories.BookCommentRepository;
import ru.otus.hw.security.BookCommentSecurity;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.BookCommentService;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Контроллер комментариев")
@WebMvcTest(BookCommentController.class)
@Import({SecurityConfiguration.class, BookCommentSecurity.class})
class BookCommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookCommentService bookCommentService;

    @MockitoBean
    private BookCommentRepository bookCommentRepository;

    @Test
    @DisplayName("POST /books/{id}/comments без аутентификации — редирект на логин")
    void shouldRedirectWhenAnonymous() throws Exception {
        mockMvc.perform(post("/books/b1/comments").param("text", "Hi"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        verify(bookCommentService, never()).create(anyString(), anyString());
    }

    @Test
    @DisplayName("ROLE_USER может добавить комментарий")
    @WithMockUser(roles = "USER")
    void userCanCreateComment() throws Exception {
        mockMvc.perform(post("/books/b1/comments").param("text", "Hi"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/b1"));

        verify(bookCommentService).create("b1", "Hi");
    }

    @Test
    @DisplayName("ROLE_USER может обновить комментарий по URL")
    @WithMockUser(roles = "USER")
    void userCanUpdateCommentByUrl() throws Exception {
        mockMvc.perform(post("/books/b1/comments/c1").param("text", "Updated"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/b1"));

        verify(bookCommentService).update("c1", "Updated");
    }

    @Test
    @DisplayName("ROLE_USER может удалить комментарий по URL")
    @WithMockUser(roles = "USER")
    void userCanDeleteCommentByUrl() throws Exception {
        mockMvc.perform(post("/books/b1/comments/c1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/b1"));

        verify(bookCommentService).deleteById("c1");
    }
}
