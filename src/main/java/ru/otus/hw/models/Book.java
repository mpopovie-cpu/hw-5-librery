package ru.otus.hw.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

// В MongoDB нет JOIN: authorId/genreId - обычные ссылки (не @DBRef).
// author/genre/comments - @Transient, наполняются сервисом отдельными
// целевыми запросами, а не автоматической подгрузкой.
@Document(collection = "books")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "title"})
public class Book {

    @Id
    private String id;

    private String title;

    @Indexed
    private String authorId;

    @Indexed
    private String genreId;

    @Transient
    private Author author;

    @Transient
    private Genre genre;

    @Transient
    private List<BookComment> comments = new ArrayList<>();

    public Book(String id, String title, Author author, Genre genre) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.authorId = author != null ? author.getId() : null;
        this.genreId = genre != null ? genre.getId() : null;
    }

    public Book(String title, Author author, Genre genre) {
        this(null, title, author, genre);
    }
}