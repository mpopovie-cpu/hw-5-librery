package ru.otus.hw.repositories;

import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Optional<Book> findById(long id);

    List<Book> findAll();

    Book save(Book book);

    void deleteById(long id);

    // догружает comments отдельным запросом в тот же persistence context,
    // чтобы не смешивать несколько коллекций в одном JOIN FETCH
    void initializeComments(Book book);
}