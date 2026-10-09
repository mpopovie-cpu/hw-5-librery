package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookComment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookCommentRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Интеграционный тест BookCommentService")
@SpringBootTest
class BookCommentServiceIntegrationTest {

    @Autowired
    private BookCommentService bookCommentService;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCommentRepository bookCommentRepository;

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

    @DisplayName("должен загружать комментарий вместе с книгой")
    @Test
    void shouldReturnCommentWithBook() {
        var saved = bookCommentRepository.save(new BookComment("Comment_1", book.getId()));

        var comment = bookCommentService.findById(saved.getId()).orElseThrow();

        assertThat(comment.getBook()).isNotNull();
        assertThat(comment.getBook().getTitle()).isEqualTo("BookTitle_1");
    }

    @DisplayName("должен загружать все комментарии книги с одной загрузкой книги на всех")
    @Test
    void shouldReturnAllCommentsForBookWithBookResolved() {
        bookCommentRepository.save(new BookComment("Comment_1", book.getId()));
        bookCommentRepository.save(new BookComment("Comment_2", book.getId()));

        var comments = bookCommentService.findAllByBookId(book.getId());

        assertThat(comments).hasSize(2)
                .allSatisfy(c -> assertThat(c.getBook()).isNotNull());
    }

    @DisplayName("должен создавать комментарий")
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldCreateComment() {
        var created = bookCommentService.create(book.getId(), "New comment");
        assertThat(created.getId()).isNotNull();
        assertThat(bookCommentRepository.findById(created.getId())).isPresent();
    }

    @DisplayName("должен выбрасывать исключение при создании комментария для несуществующей книги")
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldThrowWhenBookNotFound() {
        assertThatThrownBy(() -> bookCommentService.create("non-existent-id", "text"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("должен обновлять комментарий")
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldUpdateComment() {
        var saved = bookCommentRepository.save(new BookComment("Old text", book.getId(), "user"));
        var updated = bookCommentService.update(saved.getId(), "New text");
        assertThat(updated.getText()).isEqualTo("New text");
    }

    @DisplayName("должен удалять комментарий по id")
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void shouldDeleteComment() {
        var saved = bookCommentRepository.save(new BookComment("Comment", book.getId(), "user"));
        bookCommentService.deleteById(saved.getId());
        assertThat(bookCommentRepository.findById(saved.getId())).isEmpty();
    }
}