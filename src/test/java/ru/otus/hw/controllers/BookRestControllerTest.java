package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.models.Book;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookRestController.class)
class BookRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Test
    void shouldFindAllBooks() throws Exception {
        when(bookService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFindBookById() throws Exception {
        var book = new Book();
        book.setId("1");
        book.setTitle("Test");

        when(bookService.findById("1")).thenReturn(Optional.of(book));

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotFoundWhenBookDoesNotExist() throws Exception {
        when(bookService.findById(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateBook() throws Exception {
        var book = new Book();
        book.setId("1");
        book.setTitle("Test");

        when(bookService.insert("Test", "author1", "genre1"))
                .thenReturn(book);

        mockMvc.perform(post("/api/books")
                        .contentType("application/json")
                        .content("""
                                {
                                    "title": "Test",
                                    "authorId": "author1",
                                    "genreId": "genre1"
                                }
                                """))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldUpdateBook() throws Exception {
        var book = new Book();
        book.setId("1");
        book.setTitle("Updated");

        when(bookService.update("1", "Updated", "author1", "genre1"))
                .thenReturn(book);

        mockMvc.perform(put("/api/books/1")
                        .contentType("application/json")
                        .content("""
                                {
                                    "title": "Updated",
                                    "authorId": "author1",
                                    "genreId": "genre1"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void shouldDeleteBook() throws Exception {
        doNothing().when(bookService).deleteById("1");

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }
}