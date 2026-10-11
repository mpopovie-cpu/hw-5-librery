package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.otus.hw.clients.RecommendationClient;
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
    private final RecommendationClient recommendationClient;

    @GetMapping("/")
    public String index() {
        return "redirect:/books";
    }

    @GetMapping("/books")
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "books";
    }

    @GetMapping("/books/new")
    public String newBookForm(Model model) {
        fillForm(model, new Book());
        return "book-form";
    }

    @PostMapping("/books")
    public String createBook(@RequestParam String title,
                             @RequestParam String authorId,
                             @RequestParam String genreId,
                             Model model) {
        if (title.isBlank()) {
            return formWithError(model, null, title, authorId, genreId);
        }
        bookService.insert(title.trim(), authorId, genreId);
        return "redirect:/books";
    }

    @GetMapping("/books/{id}")
    public String viewBook(@PathVariable String id, Model model) {
        var book = bookService.findByIdWithComments(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(id)));
        
        String recommendation = recommendationClient.getRecommendation(id);
        
        model.addAttribute("book", book);
        model.addAttribute("recommendation", recommendation);
        return "book-view";
    }

    @GetMapping("/books/{id}/edit")
    public String editBookForm(@PathVariable String id, Model model) {
        var book = bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(id)));
        fillForm(model, book);
        return "book-form";
    }

    @PostMapping("/books/{id}")
    public String updateBook(@PathVariable String id,
                             @RequestParam String title,
                             @RequestParam String authorId,
                             @RequestParam String genreId,
                             Model model) {
        if (title.isBlank()) {
            return formWithError(model, id, title, authorId, genreId);
        }
        bookService.update(id, title.trim(), authorId, genreId);
        return "redirect:/books";
    }

    @PostMapping("/books/{id}/delete")
    public String deleteBook(@PathVariable String id) {
        bookService.deleteById(id);
        return "redirect:/books";
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(EntityNotFoundException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "error-page";
    }

    private void fillForm(Model model, Book book) {
        model.addAttribute("book", book);
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
    }

    private String formWithError(Model model, String id, String title, String authorId, String genreId) {
        var book = new Book();
        book.setId(id);
        book.setTitle(title);
        book.setAuthorId(authorId);
        book.setGenreId(genreId);
        fillForm(model, book);
        model.addAttribute("error", "Title must not be empty");
        return "book-form";
    }
}
