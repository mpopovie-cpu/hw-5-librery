package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookCommentRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    private final BookCommentRepository bookCommentRepository;

    @Override
    public Optional<Book> findById(String id) {
        var bookOpt = bookRepository.findById(id);
        bookOpt.ifPresent(this::fillAuthorAndGenre);
        return bookOpt;
    }

    @Override
    public Optional<Book> findByIdWithComments(String id) {
        var bookOpt = findById(id);
        bookOpt.ifPresent(book -> book.setComments(bookCommentRepository.findAllByBookId(book.getId())));
        return bookOpt;
    }

    @Override
    public List<Book> findAll() {
        var books = bookRepository.findAll();

        // без фанатизма: пакетная подгрузка авторов/жанров одним запросом на коллекцию,
        // а не по одному запросу на книгу (аналог борьбы с N+1 для MongoDB)
        var authorIds = books.stream().map(Book::getAuthorId).distinct().toList();
        var genreIds = books.stream().map(Book::getGenreId).distinct().toList();

        Map<String, Author> authorsById = authorRepository.findAllById(authorIds).stream()
                .collect(Collectors.toMap(Author::getId, a -> a));
        Map<String, Genre> genresById = genreRepository.findAllById(genreIds).stream()
                .collect(Collectors.toMap(Genre::getId, g -> g));

        books.forEach(book -> {
            book.setAuthor(authorsById.get(book.getAuthorId()));
            book.setGenre(genresById.get(book.getGenreId()));
        });
        return books;
    }

    @Override
    public Book insert(String title, String authorId, String genreId) {
        return save(null, title, authorId, genreId);
    }

    @Override
    public Book update(String id, String title, String authorId, String genreId) {
        return save(id, title, authorId, genreId);
    }

    @Override
    public void deleteById(String id) {
        // целевое удаление комментариев книги без загрузки их в память,
        // явно из сервиса (не через MongoEventListener)
        bookCommentRepository.deleteAllByBookId(id);
        bookRepository.deleteById(id);
    }

    private Book save(String id, String title, String authorId, String genreId) {
        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found".formatted(authorId)));
        var genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new EntityNotFoundException("Genre with id %s not found".formatted(genreId)));
        var book = new Book(id, title, author, genre);
        return bookRepository.save(book);
    }

    private void fillAuthorAndGenre(Book book) {
        authorRepository.findById(book.getAuthorId()).ifPresent(book::setAuthor);
        genreRepository.findById(book.getGenreId()).ifPresent(book::setGenre);
    }
}