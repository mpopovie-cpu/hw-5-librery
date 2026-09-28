package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.BookComment;
import ru.otus.hw.repositories.BookCommentRepository;
import ru.otus.hw.repositories.BookRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BookCommentServiceImpl implements BookCommentService {

    private final BookCommentRepository bookCommentRepository;

    private final BookRepository bookRepository;

    @Override
    public Optional<BookComment> findById(String id) {
        var commentOpt = bookCommentRepository.findById(id);
        commentOpt.ifPresent(comment -> bookRepository.findById(comment.getBookId()).ifPresent(comment::setBook));
        return commentOpt;
    }

    @Override
    public List<BookComment> findAllByBookId(String bookId) {
        var comments = bookCommentRepository.findAllByBookId(bookId);
        // одна и та же книга для всех комментариев - загружаем её один раз
        bookRepository.findById(bookId).ifPresent(book -> comments.forEach(c -> c.setBook(book)));
        return comments;
    }

    @Override
    public BookComment create(String bookId, String text) {
        bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(bookId)));
        var comment = new BookComment(text, bookId);
        return bookCommentRepository.save(comment);
    }

    @Override
    public BookComment update(String id, String text) {
        var comment = bookCommentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id %s not found".formatted(id)));
        comment.setText(text);
        return bookCommentRepository.save(comment);
    }

    @Override
    public void deleteById(String id) {
        bookCommentRepository.deleteById(id);
    }
}