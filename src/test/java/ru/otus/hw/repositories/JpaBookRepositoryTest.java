package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с книгами")
@DataJpaTest
@Import(JpaBookRepository.class)
class JpaBookRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private JpaBookRepository repository;

    @DisplayName("должен загружать книгу по id вместе с автором и жанром")
    @Test
    void shouldReturnCorrectBookById() {
        var expected = em.find(Book.class, 1L);

        var actual = repository.findById(1L);

        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(expected);
        assertThat(actual.get().getAuthor().getFullName()).isEqualTo(expected.getAuthor().getFullName());
        assertThat(actual.get().getGenre().getName()).isEqualTo(expected.getGenre().getName());
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actual = repository.findAll();
        assertThat(actual).hasSize(3);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var author = em.find(Author.class, 1L);
        var genre = em.find(Genre.class, 1L);
        var book = new Book("BookTitle_new", author, genre);

        var saved = repository.save(book);
        em.flush();
        em.clear();

        assertThat(saved.getId()).isGreaterThan(0);
        var fromDb = em.find(Book.class, saved.getId());
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getTitle()).isEqualTo("BookTitle_new");
    }

    @DisplayName("должен обновлять существующую книгу")
    @Test
    void shouldUpdateBook() {
        var author = em.find(Author.class, 3L);
        var genre = em.find(Genre.class, 3L);
        var book = new Book(1L, "BookTitle_updated", author, genre);

        repository.save(book);
        em.flush();
        em.clear();

        var fromDb = em.find(Book.class, 1L);
        assertThat(fromDb.getTitle()).isEqualTo("BookTitle_updated");
        assertThat(fromDb.getAuthor().getId()).isEqualTo(3L);
        assertThat(fromDb.getGenre().getId()).isEqualTo(3L);
    }

    @DisplayName("должен удалять книгу по id")
    @Test
    void shouldDeleteBook() {
        assertThat(em.find(Book.class, 1L)).isNotNull();
        repository.deleteById(1L);
        em.flush();
        em.clear();
        assertThat(em.find(Book.class, 1L)).isNull();
    }
}