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

@DisplayName("Интеграционный тест BookService")
@SpringBootTest
class BookServiceIntegrationTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookCommentRepository bookCommentRepository;

    private Author author1;

    private Author author2;

    private Genre genre1;

    private Genre genre2;

    private Book book1;

    // block() в подготовке данных допустим: это тест
    @BeforeEach
    void setUp() {
        bookCommentRepository.deleteAll().block();
        bookRepository.deleteAll().block();
        authorRepository.deleteAll().block();
        genreRepository.deleteAll().block();

        author1 = authorRepository.save(new Author("Author_1")).block();
        author2 = authorRepository.save(new Author("Author_2")).block();
        genre1 = genreRepository.save(new Genre("Genre_1")).block();
        genre2 = genreRepository.save(new Genre("Genre_2")).block();

        book1 = bookRepository.save(new Book("BookTitle_1", author1, genre1)).block();
        bookCommentRepository.save(new BookComment("Comment_1", book1.getId())).block();
        bookCommentRepository.save(new BookComment("Comment_2", book1.getId())).block();
    }

    @DisplayName("должен загружать книгу вместе с автором и жанром")
    @Test
    void shouldReturnBookWithAuthorAndGenre() {
        StepVerifier.create(bookService.findById(book1.getId()))
                .assertNext(book -> {
                    assertThat(book.getAuthor().getFullName()).isEqualTo("Author_1");
                    assertThat(book.getGenre().getName()).isEqualTo("Genre_1");
                })
                .verifyComplete();
    }

    @DisplayName("должен возвращать пустой Mono для несуществующей книги")
    @Test
    void shouldReturnEmptyWhenBookNotFound() {
        StepVerifier.create(bookService.findById("000000000000000000000000"))
                .verifyComplete();
    }

    @DisplayName("должен загружать книгу вместе с комментариями")
    @Test
    void shouldReturnBookWithComments() {
        StepVerifier.create(bookService.findByIdWithComments(book1.getId()))
                .assertNext(book -> assertThat(book.getComments()).hasSize(2))
                .verifyComplete();
    }

    @DisplayName("должен загружать все книги с пакетной подгрузкой авторов/жанров")
    @Test
    void shouldReturnAllBooksWithResolvedRelations() {
        bookRepository.save(new Book("BookTitle_2", author2, genre2)).block();

        StepVerifier.create(bookService.findAll().collectList())
                .assertNext(books -> {
                    assertThat(books).hasSize(2);
                    assertThat(books).allSatisfy(b -> {
                        assertThat(b.getAuthor()).isNotNull();
                        assertThat(b.getGenre()).isNotNull();
                    });
                })
                .verifyComplete();
    }

    @DisplayName("должен создавать книгу")
    @Test
    void shouldInsertBook() {
        StepVerifier.create(bookService.insert("New book", author2.getId(), genre2.getId()))
                .assertNext(inserted -> {
                    assertThat(inserted.getId()).isNotNull();
                    assertThat(inserted.getAuthor().getFullName()).isEqualTo("Author_2");
                })
                .verifyComplete();
    }

    @DisplayName("должен обновлять книгу")
    @Test
    void shouldUpdateBook() {
        StepVerifier.create(bookService.update(book1.getId(), "Updated title", author2.getId(), genre2.getId()))
                .assertNext(updated -> {
                    assertThat(updated.getId()).isEqualTo(book1.getId());
                    assertThat(updated.getTitle()).isEqualTo("Updated title");
                    assertThat(updated.getAuthor().getId()).isEqualTo(author2.getId());
                })
                .verifyComplete();

        StepVerifier.create(bookRepository.findById(book1.getId()))
                .assertNext(fromDb -> assertThat(fromDb.getAuthorId()).isEqualTo(author2.getId()))
                .verifyComplete();
    }

    @DisplayName("должен выбрасывать исключение при обновлении несуществующей книги")
    @Test
    void shouldThrowWhenUpdatingMissingBook() {
        StepVerifier.create(bookService.update("000000000000000000000000", "T", author1.getId(), genre1.getId()))
                .expectError(EntityNotFoundException.class)
                .verify();
    }

    @DisplayName("должен выбрасывать исключение при несуществующем авторе")
    @Test
    void shouldThrowWhenAuthorNotFound() {
        StepVerifier.create(bookService.insert("Book", "non-existent-id", genre1.getId()))
                .expectError(EntityNotFoundException.class)
                .verify();
    }

    @DisplayName("должен выбрасывать исключение при несуществующем жанре")
    @Test
    void shouldThrowWhenGenreNotFound() {
        StepVerifier.create(bookService.insert("Book", author1.getId(), "non-existent-id"))
                .expectError(EntityNotFoundException.class)
                .verify();
    }

    @DisplayName("должен удалять книгу вместе со всеми её комментариями")
    @Test
    void shouldDeleteBookWithComments() {
        StepVerifier.create(bookService.deleteById(book1.getId())).verifyComplete();

        StepVerifier.create(bookRepository.findById(book1.getId())).verifyComplete();
        StepVerifier.create(bookCommentRepository.findAllByBookId(book1.getId())).verifyComplete();
    }
}