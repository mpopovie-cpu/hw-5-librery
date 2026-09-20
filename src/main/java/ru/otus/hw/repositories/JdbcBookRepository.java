package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JdbcBookRepository implements BookRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Optional<Book> findById(long id) {
        var results = namedParameterJdbcTemplate.query(
                "select b.id, b.title, a.id as author_id, a.full_name, g.id as genre_id, g.name " +
                        "from books b " +
                        "join authors a on b.author_id = a.id " +
                        "join genres g on b.genre_id = g.id " +
                        "where b.id = :id",
                Map.of("id", id),
                new BookRowMapper()
        );
        return results.stream().findFirst();
    }

    @Override
    public List<Book> findAll() {
        return namedParameterJdbcTemplate.query(
                "select b.id, b.title, a.id as author_id, a.full_name, g.id as genre_id, g.name " +
                        "from books b " +
                        "join authors a on b.author_id = a.id " +
                        "join genres g on b.genre_id = g.id",
                new BookRowMapper()
        );
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        namedParameterJdbcTemplate.update(
                "delete from books where id = :id",
                Map.of("id", id)
        );
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(
                "insert into books(title, author_id, genre_id) values (:title, :authorId, :genreId)",
                new MapSqlParameterSource()
                        .addValue("title", book.getTitle())
                        .addValue("authorId", book.getAuthor().getId())
                        .addValue("genreId", book.getGenre().getId()),
                keyHolder
        );
        book.setId(keyHolder.getKeyAs(Long.class));
        return book;
    }

    private Book update(Book book) {
        int updated = namedParameterJdbcTemplate.update(
                "update books set title = :title, author_id = :authorId, genre_id = :genreId where id = :id",
                Map.of(
                        "title", book.getTitle(),
                        "authorId", book.getAuthor().getId(),
                        "genreId", book.getGenre().getId(),
                        "id", book.getId()
                )
        );
        if (updated == 0) {
            throw new EntityNotFoundException("Book with id %d not found".formatted(book.getId()));
        }
        return book;
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            long id = rs.getLong("id");
            String title = rs.getString("title");
            Author author = new Author(rs.getLong("author_id"), rs.getString("full_name"));
            Genre genre = new Genre(rs.getLong("genre_id"), rs.getString("name"));
            return new Book(id, title, author, genre);
        }
    }
}