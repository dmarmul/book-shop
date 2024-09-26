package org.example.bookshop.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.example.bookshop.exception.DataProcessingException;
import org.example.bookshop.model.Book;
import org.example.bookshop.repository.BookRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class BookRepositoryImpl implements BookRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Book save(Book book) {
        try {
            entityManager.persist(book);
            return book;
        } catch (Exception e) {
            throw new DataProcessingException("Can't add book: " + book, e);
        }
    }

    @Override
    @Transactional
    public List<Book> findAll() {
        try {
            return entityManager.createQuery("from Book", Book.class).getResultList();
        } catch (Exception e) {
            throw new DataProcessingException("Can't get all books from DB", e);
        }
    }
}
