package ru.otus.hw.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "books")
public class BookMongo {
    @Id
    private String id;
    private String title;
    private String authorId;
    private String genreId;

    public BookMongo() {}
    public BookMongo(String id, String title, String authorId, String genreId) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
        this.genreId = genreId;
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public String getGenreId() { return genreId; }
    public void setGenreId(String genreId) { this.genreId = genreId; }
}
