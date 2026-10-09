package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.otus.hw.models.Book;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private String id;

    private String title;

    private String authorId;

    private String authorName;

    private String genreId;

    private String genreName;

    public static BookDto from(Book book) {
        var dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthorId(book.getAuthorId());
        dto.setGenreId(book.getGenreId());

        if (book.getAuthor() != null) {
            dto.setAuthorName(book.getAuthor().getFullName());
        }

        if (book.getGenre() != null) {
            dto.setGenreName(book.getGenre().getName());
        }

        return dto;
    }
}