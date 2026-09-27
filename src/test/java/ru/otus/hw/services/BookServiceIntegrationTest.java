package ru.otus.hw.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

// без @Transactional на классе - проверяем поведение ВНЕ транзакции сервиса
@DisplayName("Интеграционный тест BookService")
@SpringBootTest
class BookServiceIntegrationTest {

    @Autowired
    private BookService bookService;

    @DisplayName("доступ к author/genre вне транзакции не должен вызывать LazyInitializationException")
    @Test
    void shouldAccessAuthorAndGenreOutsideTransaction() {
        var book = bookService.findById(1L).orElseThrow();

        assertThatCode(() -> {
            book.getAuthor().getFullName();
            book.getGenre().getName();
        }).doesNotThrowAnyException();
    }

    @DisplayName("доступ к comments вне транзакции не должен вызывать LazyInitializationException")
    @Test
    void shouldAccessCommentsOutsideTransaction() {
        var book = bookService.findByIdWithComments(1L).orElseThrow();

        assertThatCode(() -> book.getComments().size()).doesNotThrowAnyException();
        assertThat(book.getComments()).isNotEmpty();
    }

    @DisplayName("insert/update/delete книги работают корректно")
    @Test
    void shouldInsertUpdateAndDeleteBook() {
        var inserted = bookService.insert("New book", 1L, 1L);
        assertThat(inserted.getId()).isGreaterThan(0);

        var updated = bookService.update(inserted.getId(), "Updated title", 2L, 2L);
        assertThat(updated.getTitle()).isEqualTo("Updated title");

        bookService.deleteById(inserted.getId());
        assertThat(bookService.findById(inserted.getId())).isEmpty();
    }
}