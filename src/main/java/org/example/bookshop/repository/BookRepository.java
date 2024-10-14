package org.example.bookshop.repository;

import java.util.List;
import org.example.bookshop.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findAllByCategoriesId(Long categoryId);
}
