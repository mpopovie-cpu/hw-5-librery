package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookComment;
import ru.otus.hw.repositories.BookCommentRepository;
import ru.otus.hw.repositories.BookRepository;

@Service
@RequiredArgsConstructor
public class BookCommentServiceImpl implements BookCommentService {

    private final BookCommentRepository bookCommentRepository;

    private final BookRepository bookRepository;

    @Override
    public Mono<BookComment> findById(String id) {
        return bookCommentRepository.findById(id)
                .flatMap(comment -> requireBook(comment.getBookId())
                        .map(book -> {
                            comment.setBook(book);
                            return comment;
                        }));
    }

    // Книга загружается один раз на все комментарии
    @Override
    public Flux<BookComment> findAllByBookId(String bookId) {
        return bookRepository.findById(bookId)
                .flatMapMany(book -> bookCommentRepository.findAllByBookId(bookId)
                        .map(comment -> {
                            comment.setBook(book);
                            return comment;
                        }));
    }

    @Override
    public Mono<BookComment> create(String bookId, String text) {
        return requireBook(bookId)
                .flatMap(book -> bookCommentRepository.save(new BookComment(text, bookId))
                        .map(saved -> {
                            saved.setBook(book);
                            return saved;
                        }));
    }

    @Override
    public Mono<BookComment> update(String id, String text) {
        return bookCommentRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Comment not found: " + id)))
                .flatMap(comment -> {
                    comment.setText(text);
                    return bookCommentRepository.save(comment);
                });
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return bookCommentRepository.deleteById(id);
    }

    private Mono<Book> requireBook(String bookId) {
        return bookRepository.findById(bookId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Book not found: " + bookId)));
    }
}