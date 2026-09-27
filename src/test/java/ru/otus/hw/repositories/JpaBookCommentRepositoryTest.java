package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookComment;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с комментариями")
@DataJpaTest
@Import(JpaBookCommentRepository.class)
class JpaBookCommentRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private JpaBookCommentRepository repository;

    @DisplayName("должен загружать комментарий по id вместе с книгой")
    @Test
    void shouldReturnCorrectCommentById() {
        var expected = em.find(BookComment.class, 1L);

        var actual = repository.findById(1L);

        assertThat(actual).isPresent().get().isEqualTo(expected);
        assertThat(actual.get().getBook().getId()).isEqualTo(expected.getBook().getId());
    }

    @DisplayName("должен возвращать пустой Optional для несуществующего id")
    @Test
    void shouldReturnEmptyForNonExistentId() {
        assertThat(repository.findById(999L)).isEmpty();
    }

    @DisplayName("должен загружать все комментарии по id книги")
    @Test
    void shouldReturnAllCommentsByBookId() {
        var actual = repository.findAllByBookId(1L);
        assertThat(actual).hasSize(2)
                .allMatch(c -> c.getBook().getId() == 1L);
    }

    @DisplayName("должен сохранять новый комментарий")
    @Test
    void shouldSaveNewComment() {
        var book = em.find(Book.class, 2L);
        var comment = new BookComment("new comment", book);

        var saved = repository.save(comment);
        em.flush();
        em.clear();

        assertThat(saved.getId()).isGreaterThan(0);
        var fromDb = em.find(BookComment.class, saved.getId());
        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getText()).isEqualTo("new comment");
    }

    @DisplayName("должен удалять комментарий по id")
    @Test
    void shouldDeleteComment() {
        assertThat(em.find(BookComment.class, 1L)).isNotNull();
        repository.deleteById(1L);
        em.flush();
        em.clear();
        assertThat(em.find(BookComment.class, 1L)).isNull();
    }
}