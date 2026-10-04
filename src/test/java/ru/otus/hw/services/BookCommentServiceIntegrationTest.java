package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;
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

    // block() в подготовке данных допустим: это тест
    @BeforeEach
    void setUp() {
        bookCommentRepository.deleteAll().block();
        bookRepository.deleteAll().block();
        authorRepository.deleteAll().block();
        genreRepository.deleteAll().block();

        var author = authorRepository.save(new Author("Author_1")).block();
        var genre = genreRepository.save(new Genre("Genre_1")).block();
        book = bookRepository.save(new Book("BookTitle_1", author, genre)).block();
    }

    @DisplayName("должен загружать комментарий вместе с книгой")
    @Test
    void shouldReturnCommentWithBook() {
        var saved = bookCommentRepository.save(new BookComment("Comment_1", book.getId())).block();

        StepVerifier.create(bookCommentService.findById(saved.getId()))
                .assertNext(comment -> {
                    assertThat(comment.getBook()).isNotNull();
                    assertThat(comment.getBook().getTitle()).isEqualTo("BookTitle_1");
                })
                .verifyComplete();
    }

    @DisplayName("должен загружать все комментарии книги с одной загрузкой книги на всех")
    @Test
    void shouldReturnAllCommentsForBookWithBookResolved() {
        bookCommentRepository.save(new BookComment("Comment_1", book.getId())).block();
        bookCommentRepository.save(new BookComment("Comment_2", book.getId())).block();

        StepVerifier.create(bookCommentService.findAllByBookId(book.getId()).collectList())
                .assertNext(comments -> assertThat(comments).hasSize(2)
                        .allSatisfy(c -> assertThat(c.getBook()).isNotNull()))
                .verifyComplete();
    }

    @DisplayName("должен создавать комментарий")
    @Test
    void shouldCreateComment() {
        StepVerifier.create(bookCommentService.create(book.getId(), "New comment"))
                .assertNext(created -> assertThat(created.getId()).isNotNull())
                .verifyComplete();

        StepVerifier.create(bookCommentRepository.findAllByBookId(book.getId()))
                .expectNextCount(1)
                .verifyComplete();
    }

    @DisplayName("должен выбрасывать исключение при создании комментария для несуществующей книги")
    @Test
    void shouldThrowWhenBookNotFound() {
        StepVerifier.create(bookCommentService.create("non-existent-id", "text"))
                .expectError(EntityNotFoundException.class)
                .verify();
    }

    @DisplayName("должен обновлять комментарий")
    @Test
    void shouldUpdateComment() {
        var saved = bookCommentRepository.save(new BookComment("Old text", book.getId())).block();

        StepVerifier.create(bookCommentService.update(saved.getId(), "New text"))
                .assertNext(updated -> assertThat(updated.getText()).isEqualTo("New text"))
                .verifyComplete();
    }

    @DisplayName("должен удалять комментарий по id")
    @Test
    void shouldDeleteComment() {
        var saved = bookCommentRepository.save(new BookComment("Comment", book.getId())).block();

        StepVerifier.create(bookCommentService.deleteById(saved.getId())).verifyComplete();
        StepVerifier.create(bookCommentRepository.findById(saved.getId())).verifyComplete();
    }
}