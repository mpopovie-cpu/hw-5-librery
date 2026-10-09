package ru.otus.hw.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "authors")
public class AuthorMongo {
    @Id
    private String id;
    private String name;
    private List<String> bookIds;

    public AuthorMongo() {}
    public AuthorMongo(String id, String name, List<String> bookIds) {
        this.id = id;
        this.name = name;
        this.bookIds = bookIds;
    }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<String> getBookIds() { return bookIds; }
    public void setBookIds(List<String> bookIds) { this.bookIds = bookIds; }
}
