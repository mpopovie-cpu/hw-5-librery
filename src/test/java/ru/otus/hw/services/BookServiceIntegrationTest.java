package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

    @BeforeEach
    void setUp() {
        bookCommentRepository.deleteAll();
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();

        author1 = authorRepository.save(new Author("Author_1"));
        author2 = authorRepository.save(new Author("Author_2"));
        genre1 = genreRepository.save(new Genre("Genre_1"));
        genre2 = genreRepository.save(new Genre("Genre_2"));

        book1 = bookRepository.save(new Book("BookTitle_1", author1, genre1));
        bookCommentRepository.save(new BookComment("Comment_1", book1.getId()));
        bookCommentRepository.save(new BookComment("Comment_2", book1.getId()));
    }

    @DisplayName("должен загружать книгу вместе с автором и жанром")
    @Test
    void shouldReturnBookWithAuthorAndGenre() {
        var book = bookService.findById(book1.getId()).orElseThrow();
        assertThat(book.getAuthor().getFullName()).isEqualTo("Author_1");
        assertThat(book.getGenre().getName()).isEqualTo("Genre_1");
    }

    @DisplayName("должен загружать книгу вместе с комментариями")
    @Test
    void shouldReturnBookWithComments() {
        var book = bookService.findByIdWithComments(book1.getId()).orElseThrow();
        assertThat(book.getComments()).hasSize(2);
    }

    @DisplayName("должен загружать все книги с пакетной подгрузкой авторов/жанров")
    @Test
    void shouldReturnAllBooksWithResolvedRelations() {
        bookRepository.save(new Book("BookTitle_2", author2, genre2));
        var books = bookService.findAll();
        assertThat(books).hasSize(2);
        assertThat(books).allSatisfy(b -> {
            assertThat(b.getAuthor()).isNotNull();
            assertThat(b.getGenre()).isNotNull();
        });
    }

    @DisplayName("должен создавать книгу")
    @Test
    void shouldInsertBook() {
        var inserted = bookService.insert("New book", author2.getId(), genre2.getId());
        assertThat(inserted.getId()).isNotNull();
        assertThat(bookService.findById(inserted.getId())).isPresent();
    }

    @DisplayName("должен обновлять книгу")
    @Test
    void shouldUpdateBook() {
        var updated = bookService.update(book1.getId(), "Updated title", author2.getId(), genre2.getId());
        assertThat(updated.getTitle()).isEqualTo("Updated title");
        var fromDb = bookRepository.findById(book1.getId()).orElseThrow();
        assertThat(fromDb.getAuthorId()).isEqualTo(author2.getId());
    }

    @DisplayName("должен выбрасывать исключение при несуществующем авторе")
    @Test
    void shouldThrowWhenAuthorNotFound() {
        assertThatThrownBy(() -> bookService.insert("Book", "non-existent-id", genre1.getId()))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("должен удалять книгу вместе со всеми её комментариями")
    @Test
    void shouldDeleteBookWithComments() {
        assertThat(bookCommentRepository.findAllByBookId(book1.getId())).hasSize(2);

        bookService.deleteById(book1.getId());

        assertThat(bookRepository.findById(book1.getId())).isEmpty();
        assertThat(bookCommentRepository.findAllByBookId(book1.getId())).isEmpty();
    }
}