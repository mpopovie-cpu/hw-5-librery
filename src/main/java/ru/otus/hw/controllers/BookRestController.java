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
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookRequestDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.BookService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BookRestController {

    private final BookService bookService;

    @GetMapping
    public List<BookDto> findAll() {
        return bookService.findAll().stream()
                .map(BookDto::from)
                .toList();
    }

    @GetMapping("/{id}")
    public BookDto findById(@PathVariable String id) {
        return bookService.findById(id)
                .map(BookDto::from)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Book with id %s not found".formatted(id)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto create(@RequestBody BookRequestDto request) {
        var book = bookService.insert(
                request.getTitle(),
                request.getAuthorId(),
                request.getGenreId());

        return BookDto.from(book);
    }

    @PutMapping("/{id}")
    public BookDto update(@PathVariable String id,
                          @RequestBody BookRequestDto request) {
        var book = bookService.update(
                id,
                request.getTitle(),
                request.getAuthorId(),
                request.getGenreId());

        return BookDto.from(book);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        bookService.deleteById(id);
    }
}