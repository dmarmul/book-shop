package org.example.bookshop.repository;

import java.util.List;
import java.util.Optional;
import org.example.bookshop.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface BookRepository extends JpaRepository<Book, Long> {
    @EntityGraph(attributePaths = {"categories"})
    @NonNull
    @Override
    Optional<Book> findById(@NonNull Long id);

    @EntityGraph(attributePaths = {"categories"})
    @NonNull
    @Override
    Page<Book> findAll(@NonNull Pageable pageable);

    List<Book> findAllByCategoriesId(Long categoryId);
}
