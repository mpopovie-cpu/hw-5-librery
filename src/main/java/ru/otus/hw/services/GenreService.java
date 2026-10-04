package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Genre;

public interface GenreService {

    Mono<Genre> findById(String id);

    Flux<Genre> findAll();

    Mono<Genre> insert(Genre genre);

    Mono<Genre> update(Genre genre);

    Mono<Void> deleteById(String id);
}