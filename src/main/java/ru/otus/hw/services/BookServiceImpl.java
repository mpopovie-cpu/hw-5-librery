package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookCommentRepository bookCommentRepository;

    @Override
    public Mono<Book> findById(String id) {
        return resolveRelations(bookRepository.findById(id).flux()).next();
    }

    @Override
    public Mono<Book> findByIdWithComments(String id) {
        return findById(id)
                .flatMap(book -> bookCommentRepository.findAllByBookId(id)
                        .collectList()
                        .map(comments -> {
                            book.setComments(comments);
                            return book;
                        }));
    }

    @Override
    public Flux<Book> findAll() {
        return resolveRelations(bookRepository.findAll());
    }

    @Override
    public Mono<Book> insert(String title, String authorId, String genreId) {
        return Mono.zip(requireAuthor(authorId), requireGenre(genreId))
                .flatMap(relations -> bookRepository.save(new Book(title, relations.getT1(), relations.getT2())))
                .flatMap(saved -> findById(saved.getId()));
    }

    @Override
    public Mono<Book> update(String id, String title, String authorId, String genreId) {
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Book not found: " + id)))
                .zipWith(Mono.zip(requireAuthor(authorId), requireGenre(genreId)))
                .flatMap(tuple -> {
                    var book = tuple.getT1();
                    book.setTitle(title);
                    book.setAuthorId(tuple.getT2().getT1().getId());
                    book.setGenreId(tuple.getT2().getT2().getId());
                    return bookRepository.save(book);
                })
                .flatMap(saved -> findById(saved.getId()));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return bookCommentRepository.deleteAllByBookId(id)
                .then(bookRepository.deleteById(id));
    }

    private Mono<Author> requireAuthor(String authorId) {
        return authorRepository.findById(authorId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Author not found: " + authorId)));
    }

    private Mono<Genre> requireGenre(String genreId) {
        return genreRepository.findById(genreId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Genre not found: " + genreId)));
    }

    // Проблема N+1: авторы и жанры всех книг подгружаются двумя запросами (findAllById), а не по запросу на книгу.
    private Flux<Book> resolveRelations(Flux<Book> books) {
        return books.collectList().flatMapMany(list -> {
            if (list.isEmpty()) {
                return Flux.<Book>empty();
            }
            var authors = authorRepository.findAllById(ids(list, Book::getAuthorId))
                    .collectMap(Author::getId);
            var genres = genreRepository.findAllById(ids(list, Book::getGenreId))
                    .collectMap(Genre::getId);
            return Mono.zip(authors, genres)
                    .flatMapMany(maps -> Flux.fromIterable(attach(list, maps.getT1(), maps.getT2())));
        });
    }

    private static Set<String> ids(List<Book> books, Function<Book, String> getter) {
        return books.stream().map(getter).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    private static List<Book> attach(List<Book> books, Map<String, Author> authors, Map<String, Genre> genres) {
        books.forEach(book -> {
            book.setAuthor(authors.get(book.getAuthorId()));
            book.setGenre(genres.get(book.getGenreId()));
        });
        return books;
    }
}