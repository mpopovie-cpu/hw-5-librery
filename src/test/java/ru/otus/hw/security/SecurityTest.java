package ru.otus.hw.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "USER")
    public void testUserCanAccessBooks() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testUserCannotCreateBook() throws Exception {
        mockMvc.perform(post("/books"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAdminCanCreateBook() throws Exception {
        mockMvc.perform(post("/books")
                .param("title", "Test Book")
                .param("authorId", "1")
                .param("genreId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUnauthenticatedCannotAccessBooks() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().is3xxRedirection());
    }
}
