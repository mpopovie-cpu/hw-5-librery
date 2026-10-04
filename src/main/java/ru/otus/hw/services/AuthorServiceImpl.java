package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Override
    public Mono<Author> findById(String id) {
        return authorRepository.findById(id);
    }

    @Override
    public Flux<Author> findAll() {
        return authorRepository.findAll();
    }

    @Override
    public Mono<Author> insert(Author author) {
        return authorRepository.save(author);
    }

    @Override
    public Mono<Author> update(Author author) {
        return authorRepository.save(author);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return authorRepository.deleteById(id);
    }
}