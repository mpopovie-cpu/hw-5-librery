package ru.otus.hw.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookRequestDto {

    private String title;
    private String authorId;
    private String genreId;
}