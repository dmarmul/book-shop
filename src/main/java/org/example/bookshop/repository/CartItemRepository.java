package org.example.bookshop.repository;

import java.util.Optional;
import java.util.Set;
import org.example.bookshop.model.CartItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT ci FROM CartItem ci JOIN ci.shoppingCart sc "
            + "WHERE ci.id = :cartItemId AND sc.user.id = :userId")
    Optional<CartItem> findByIdAndUserId(Long cartItemId, Long userId);

    @EntityGraph(attributePaths = {"shoppingCart", "book"})
    Set<CartItem> findAllByShoppingCartId(Long id);
}
