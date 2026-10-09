package ru.otus.hw.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Интеграционные тесты аутентификации и защиты ресурсов")
@SpringBootTest
@AutoConfigureMockMvc
class SecurityAuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("страница /login должна быть доступна анонимному пользователю")
    void loginPageShouldBePubliclyAccessible() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("успешная аутентификация пользователя через форму логина")
    void shouldAuthenticateSuccessfully() throws Exception {
        mockMvc.perform(formLogin().user("user").password("password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(authenticated().withUsername("user"));
    }

    @Test
    @DisplayName("ошибка аутентификации при неверном пароле")
    void shouldFailAuthenticationWithBadCredentials() throws Exception {
        mockMvc.perform(formLogin().user("user").password("wrong-password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("неавторизованный запрос к /books должен перенаправлять на /login")
    void shouldRedirectToLoginWhenAccessingProtectedResource() throws Exception {
        mockMvc.perform(get("/books"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @DisplayName("авторизованный пользователь получает доступ к защищенному ресурсу")
    void shouldAllowAccessForAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/books").with(user("user")))
                .andExpect(status().isOk());
    }
}
