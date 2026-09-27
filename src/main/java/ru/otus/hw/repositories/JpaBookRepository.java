package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaBookRepository implements BookRepository {

    private final EntityManager entityManager;

    public JpaBookRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Book> findById(long id) {
        var query = entityManager.createQuery(
                "select b from Book b join fetch b.author join fetch b.genre where b.id = :id",
                Book.class);
        query.setParameter("id", id);
        return query.getResultStream().findFirst();
    }

    @Override
    public List<Book> findAll() {
        return entityManager.createQuery(
                        "select b from Book b join fetch b.author join fetch b.genre",
                        Book.class)
                .getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            entityManager.persist(book);
            return book;
        }
        return entityManager.merge(book);
    }

    @Override
    public void deleteById(long id) {
        var book = entityManager.find(Book.class, id);
        if (book != null) {
            entityManager.remove(book);
        }
    }

    @Override
    public void initializeComments(Book book) {
        // отдельный запрос, чтобы не плодить декартово произведение
        // author/genre x comments в одном JOIN FETCH
        entityManager.createQuery(
                        "select b from Book b left join fetch b.comments where b.id = :id",
                        Book.class)
                .setParameter("id", book.getId())
                .getResultList();
    }
}