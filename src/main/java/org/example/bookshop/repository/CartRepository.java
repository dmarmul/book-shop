package org.example.bookshop.repository;

import java.util.Optional;
import org.example.bookshop.model.ShoppingCart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<ShoppingCart, Long> {
    @EntityGraph(attributePaths = {"user", "user.roles", "cartItems", "cartItems.book"})
    Optional<ShoppingCart> findByUserId(Long id);
}
