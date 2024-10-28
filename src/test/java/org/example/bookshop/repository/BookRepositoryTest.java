package org.example.bookshop.repository;

import java.util.List;
import org.example.bookshop.model.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "classpath:database/books/delete-all-books-from-books-table.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class BookRepositoryTest {
    private static final Long BOOK_ID = 1L;
    private static final int NUMBER_OF_BOOKS = 2;
    @Autowired
    private BookRepository bookRepository;

    @Test
    @Sql(scripts = "classpath:database/books/add-book-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-book-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Find book by id")
    void findById_ReturnsOneBook() {
        Book actualBook = bookRepository.findById(BOOK_ID).orElseThrow(() ->
                new RuntimeException("Can't find book by id: " + BOOK_ID)
        );

        Assertions.assertEquals(BOOK_ID, actualBook.getId());
    }

    @Test
    @Sql(scripts = "classpath:database/books/add-two-books-to-books-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/books/delete-two-books-from-books-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Find all books")
    void findAll_ReturnsTwoBooks() {
        List<Book> actualBooks = bookRepository.findAll(PageRequest.of(0, 10)).toList();

        Assertions.assertEquals(NUMBER_OF_BOOKS, actualBooks.size());
    }
}
