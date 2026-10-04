package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.BookService;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookRestController {

    private final BookService bookService;

    @GetMapping
    public Flux<BookDto> findAll() {
        return bookService.findAll().map(BookDto::from);
    }

    @GetMapping("/{id}")
    public Mono<BookDto> findById(@PathVariable String id) {
        return bookService.findById(id)
                .map(BookDto::from)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Book not found: " + id)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BookDto> create(@RequestBody BookRequestDto request) {
        return bookService.insert(request.getTitle(), request.getAuthorId(), request.getGenreId())
                .map(BookDto::from);
    }

    @PutMapping("/{id}")
    public Mono<BookDto> update(@PathVariable String id, @RequestBody BookRequestDto request) {
        return bookService.update(id, request.getTitle(), request.getAuthorId(), request.getGenreId())
                .map(BookDto::from);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return bookService.deleteById(id);
    }
}