package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jpa для работы с жанрами")
@DataJpaTest
@Import(JpaGenreRepository.class)
class JpaGenreRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private JpaGenreRepository repository;

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnCorrectGenresList() {
        var expected = em.find(Genre.class, 1L);
        var actual = repository.findAll();
        assertThat(actual).hasSize(3).contains(expected);
    }

    @DisplayName("должен загружать жанр по id")
    @Test
    void shouldReturnCorrectGenreById() {
        var expected = em.find(Genre.class, 2L);
        var actual = repository.findById(2L);
        assertThat(actual).isPresent().get().isEqualTo(expected);
    }

    @DisplayName("должен возвращать пустой Optional для несуществующего id")
    @Test
    void shouldReturnEmptyForNonExistentId() {
        assertThat(repository.findById(999L)).isEmpty();
    }
}