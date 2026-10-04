package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Author;

public interface AuthorService {

    Mono<Author> findById(String id);

    Flux<Author> findAll();

    Mono<Author> insert(Author author);

    Mono<Author> update(Author author);

    Mono<Void> deleteById(String id);
}