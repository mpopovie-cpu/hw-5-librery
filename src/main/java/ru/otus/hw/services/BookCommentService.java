package ru.otus.hw.services;

import ru.otus.hw.models.BookComment;

import java.util.List;
import java.util.Optional;

public interface BookCommentService {
    Optional<BookComment> findById(long id);

    List<BookComment> findAllByBookId(long bookId);

    BookComment create(long bookId, String text);

    BookComment update(long id, String text);

    void deleteById(long id);
}