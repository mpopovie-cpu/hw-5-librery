package ru.otus.hw.converters;

import org.springframework.stereotype.Component;
import ru.otus.hw.models.BookComment;

@Component
public class BookCommentConverter {
    public String commentToString(BookComment comment) {
        return "Id: %d, text: %s, bookId: %d".formatted(
                comment.getId(), comment.getText(), comment.getBook().getId());
    }
}