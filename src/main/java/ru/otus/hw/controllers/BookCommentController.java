package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.services.BookCommentService;

@RequiredArgsConstructor
@Controller
public class BookCommentController {

    private final BookCommentService bookCommentService;

    @PostMapping("/books/{bookId}/comments")
    public String createComment(@PathVariable String bookId, @RequestParam String text) {
        if (!text.isBlank()) {
            bookCommentService.create(bookId, text.trim());
        }
        return "redirect:/books/" + bookId;
    }

    @PostMapping("/books/{bookId}/comments/{commentId}")
    public String updateComment(@PathVariable String bookId,
                                @PathVariable String commentId,
                                @RequestParam String text) {
        if (!text.isBlank()) {
            bookCommentService.update(commentId, text.trim());
        }
        return "redirect:/books/" + bookId;
    }

    @PostMapping("/books/{bookId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable String bookId, @PathVariable String commentId) {
        bookCommentService.deleteById(commentId);
        return "redirect:/books/" + bookId;
    }
}
