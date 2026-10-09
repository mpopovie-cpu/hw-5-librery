package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.*;
import ru.otus.hw.repositories.*;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;

    @Override
    public void run(String... args) {
        bookRepository.deleteAll();
        Genre g = genreRepository.save(new Genre("Fantasy"));
        Author a = authorRepository.save(new Author("Tolkien"));
        Book b = new Book(); b.setTitle("The Hobbit"); b.setAuthorId(a.getId()); b.setGenreId(g.getId());
        bookRepository.save(b);
    }
}
