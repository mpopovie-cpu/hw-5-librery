package ru.otus.hw.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.otus.hw.repositories.BookCommentRepository;

@Component("bookCommentSecurity")
@RequiredArgsConstructor
public class BookCommentSecurity {

    private final BookCommentRepository bookCommentRepository;

    public boolean isOwner(String commentId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        var username = authentication.getName();
        return bookCommentRepository.findById(commentId)
                .map(comment -> username.equals(comment.getAuthorUsername()))
                .orElse(false);
    }
}
