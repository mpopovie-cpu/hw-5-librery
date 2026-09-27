package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.BookComment;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaBookCommentRepository implements BookCommentRepository {

    private final EntityManager entityManager;

    public JpaBookCommentRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<BookComment> findById(long id) {
        var query = entityManager.createQuery(
                "select c from BookComment c join fetch c.book where c.id = :id",
                BookComment.class);
        query.setParameter("id", id);
        return query.getResultStream().findFirst();
    }

    @Override
    public List<BookComment> findAllByBookId(long bookId) {
        var query = entityManager.createQuery(
                "select c from BookComment c join fetch c.book where c.book.id = :bookId",
                BookComment.class);
        query.setParameter("bookId", bookId);
        return query.getResultList();
    }

    @Override
    public BookComment save(BookComment comment) {
        if (comment.getId() == 0) {
            entityManager.persist(comment);
            return comment;
        }
        return entityManager.merge(comment);
    }

    @Override
    public void deleteById(long id) {
        var comment = entityManager.find(BookComment.class, id);
        if (comment != null) {
            entityManager.remove(comment);
        }
    }
}