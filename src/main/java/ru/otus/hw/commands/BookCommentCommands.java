package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.BookCommentConverter;
import ru.otus.hw.services.BookCommentService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class BookCommentCommands {

    private final BookCommentService bookCommentService;

    private final BookCommentConverter commentConverter;

    @ShellMethod(value = "Find comment by id", key = "cbid")
    public String findCommentById(String id) {
        return bookCommentService.findById(id)
                .map(commentConverter::commentToString)
                .orElse("Comment with id %s not found".formatted(id));
    }

    @ShellMethod(value = "Find comments by book id", key = "cab")
    public String findCommentsByBookId(String bookId) {
        return bookCommentService.findAllByBookId(bookId).stream()
                .map(commentConverter::commentToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    // cins <bookId> newComment
    @ShellMethod(value = "Insert comment", key = "cins")
    public String insertComment(String bookId, String text) {
        return commentConverter.commentToString(bookCommentService.create(bookId, text));
    }

    // cupd <id> editedComment
    @ShellMethod(value = "Update comment", key = "cupd")
    public String updateComment(String id, String text) {
        return commentConverter.commentToString(bookCommentService.update(id, text));
    }

    // cdel <id>
    @ShellMethod(value = "Delete comment by id", key = "cdel")
    public void deleteComment(String id) {
        bookCommentService.deleteById(id);
    }
}