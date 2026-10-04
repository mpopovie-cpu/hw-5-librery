package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    @Override
    public Mono<Genre> findById(String id) {
        return genreRepository.findById(id);
    }

    @Override
    public Flux<Genre> findAll() {
        return genreRepository.findAll();
    }

    @Override
    public Mono<Genre> insert(Genre genre) {
        return genreRepository.save(genre);
    }

    @Override
    public Mono<Genre> update(Genre genre) {
        return genreRepository.save(genre);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return genreRepository.deleteById(id);
    }
}