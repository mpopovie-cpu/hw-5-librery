package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

@RequiredArgsConstructor
@Controller
public class BookController {

    private final BookService bookService;

    private final AuthorService authorService;

    private final GenreService genreService;

    @GetMapping("/")
    public String index() {
        return "redirect:/books";
    }

    @GetMapping("/books")
    public String listBooks() {
        return "books";
    }

    @GetMapping("/books/new")
    public String newBookForm(Model model) {
        model.addAttribute("book", new Book());
        addAuthorsAndGenres(model);
        return "book-form";
    }

    @GetMapping("/books/{id}")
    public Mono<String> viewBook(@PathVariable String id, Model model) {
        return bookService.findByIdWithComments(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Book not found: " + id)))
                .doOnNext(book -> model.addAttribute("book", book))
                .thenReturn("book-view");
    }

    @GetMapping("/books/{id}/edit")
    public Mono<String> editBookForm(@PathVariable String id, Model model) {
        return bookService.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Book not found: " + id)))
                .doOnNext(book -> {
                    model.addAttribute("book", book);
                    addAuthorsAndGenres(model);
                })
                .thenReturn("book-form");
    }

    // Flux в модели WebFlux разворачивает в список перед рендерингом шаблона
    private void addAuthorsAndGenres(Model model) {
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
    }
}