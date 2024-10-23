package org.example.bookshop.repository;

import java.util.Optional;
import java.util.Set;
import org.example.bookshop.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT i FROM OrderItem i WHERE i.order.id = :orderId AND i.order.user.id = :userId")
    Set<OrderItem> findAllByOrderIdUserId(Long orderId, Long userId);

    @Query("SELECT i FROM OrderItem i "
            + "WHERE i.id = :itemId AND i.order.id = :orderId AND i.order.user.id = :userId")
    Optional<OrderItem> findByIdAndOrderIdAndUserId(Long itemId, Long orderId, Long userId);
}
