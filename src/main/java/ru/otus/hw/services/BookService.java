package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Book;

public interface BookService {

    Mono<Book> findById(String id);

    Mono<Book> findByIdWithComments(String id);

    Flux<Book> findAll();

    Mono<Book> insert(String title, String authorId, String genreId);

    Mono<Book> update(String id, String title, String authorId, String genreId);

    Mono<Void> deleteById(String id);
}