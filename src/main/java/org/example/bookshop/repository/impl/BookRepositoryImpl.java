package org.example.bookshop.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.bookshop.exception.DataProcessingException;
import org.example.bookshop.exception.EntityNotFoundException;
import org.example.bookshop.model.Book;
import org.example.bookshop.repository.BookRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {
    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public Book save(Book book) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = entityManager.unwrap(Session.class);
            transaction = session.beginTransaction();
            session.save(book);
            transaction.commit();
            return book;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can't add book: " + book, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public Optional<Book> findById(Long id) {
        try (Session session = entityManager.unwrap(Session.class)) {
            return Optional.ofNullable(session.find(Book.class, id));
        }
    }

    @Override
    public List<Book> findAll() {
        try (Session session = entityManager.unwrap(Session.class)) {
            return session.createQuery("from Book", Book.class).getResultList();
        } catch (Exception e) {
            throw new EntityNotFoundException("Can't get all books from DB", e);
        }
    }
}
