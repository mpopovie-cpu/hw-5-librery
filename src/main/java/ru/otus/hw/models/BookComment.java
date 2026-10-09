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

// book - @Transient, наполняется сервисом отдельным запросом
@Document(collection = "book_comments")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "text", "authorUsername"})
public class BookComment {

    @Id
    private String id;

    private String text;

    @Indexed
    private String bookId;

    private String authorUsername;

    @Transient
    private Book book;

    public BookComment(String text, String bookId) {
        this.text = text;
        this.bookId = bookId;
    }

    public BookComment(String text, String bookId, String authorUsername) {
        this.text = text;
        this.bookId = bookId;
        this.authorUsername = authorUsername;
    }

    public BookComment(String id, String text, String bookId, String authorUsername) {
        this.id = id;
        this.text = text;
        this.bookId = bookId;
        this.authorUsername = authorUsername;
    }
}