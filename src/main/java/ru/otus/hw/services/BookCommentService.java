package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.BookComment;

public interface BookCommentService {

    Mono<BookComment> findById(String id);

    Flux<BookComment> findAllByBookId(String bookId);

    Mono<BookComment> create(String bookId, String text);

    Mono<BookComment> update(String id, String text);

    Mono<Void> deleteById(String id);
}