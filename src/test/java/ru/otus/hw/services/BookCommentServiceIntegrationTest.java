package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

// без @Transactional на классе - проверяем поведение ВНЕ транзакции сервиса
@DisplayName("Интеграционный тест BookCommentService")
@SpringBootTest
class BookCommentServiceIntegrationTest {

    @Autowired
    private BookCommentService bookCommentService;

    @DisplayName("доступ к book вне транзакции не должен вызывать LazyInitializationException")
    @Test
    void shouldAccessBookOutsideTransaction() {
        var comment = bookCommentService.findById(1L).orElseThrow();

        assertThatCode(() -> comment.getBook().getTitle()).doesNotThrowAnyException();
    }

    @DisplayName("список комментариев книги доступен вне транзакции без исключений")
    @Test
    void shouldAccessBookForAllCommentsOutsideTransaction() {
        var comments = bookCommentService.findAllByBookId(1L);

        assertThat(comments).isNotEmpty();
        assertThatCode(() -> comments.forEach(c -> c.getBook().getId())).doesNotThrowAnyException();
    }

    @DisplayName("create/update/delete комментария работают корректно")
    @Test
    void shouldCreateUpdateAndDeleteComment() {
        var created = bookCommentService.create(2L, "temp comment");
        assertThat(created.getId()).isGreaterThan(0);

        var updated = bookCommentService.update(created.getId(), "updated comment");
        assertThat(updated.getText()).isEqualTo("updated comment");

        bookCommentService.deleteById(created.getId());
        assertThat(bookCommentService.findById(created.getId())).isEmpty();
    }
}