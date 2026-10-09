package ru.otus.hw.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookComment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookCommentRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.services.BookCommentService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Авторизация комментариев на уровне сервиса")
@SpringBootTest
class BookCommentMethodSecurityIntegrationTest {

    @Autowired
    private BookCommentService bookCommentService;

    @Autowired
    private BookCommentRepository bookCommentRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    private Book book;

    @BeforeEach
    void setUp() {
        bookCommentRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();

        var author = authorRepository.save(new Author("Author_1"));
        var genre = genreRepository.save(new Genre("Genre_1"));
        book = bookRepository.save(new Book("BookTitle_1", author, genre));
    }

    @Test
    @DisplayName("владелец может обновить свой комментарий")
    @WithMockUser(username = "user", roles = "USER")
    void ownerCanUpdateComment() {
        var saved = bookCommentRepository.save(new BookComment("Old", book.getId(), "user"));

        var updated = bookCommentService.update(saved.getId(), "New");

        assertThat(updated.getText()).isEqualTo("New");
    }

    @Test
    @DisplayName("чужой пользователь не может обновить комментарий")
    @WithMockUser(username = "other", roles = "USER")
    void strangerCannotUpdateComment() {
        var saved = bookCommentRepository.save(new BookComment("Old", book.getId(), "user"));

        assertThatThrownBy(() -> bookCommentService.update(saved.getId(), "Hack"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("ADMIN может обновить чужой комментарий")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCanUpdateForeignComment() {
        var saved = bookCommentRepository.save(new BookComment("Old", book.getId(), "user"));

        var updated = bookCommentService.update(saved.getId(), "Moderated");

        assertThat(updated.getText()).isEqualTo("Moderated");
    }

    @Test
    @DisplayName("создание комментария сохраняет имя автора")
    @WithMockUser(username = "user", roles = "USER")
    void createSetsAuthorUsername() {
        var created = bookCommentService.create(book.getId(), "My comment");

        assertThat(created.getAuthorUsername()).isEqualTo("user");
    }
}
