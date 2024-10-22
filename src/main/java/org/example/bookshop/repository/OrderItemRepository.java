package org.example.bookshop.repository;

import java.util.Optional;
import java.util.Set;
import org.example.bookshop.model.OrderItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @EntityGraph(attributePaths = {"order"})
    Optional<OrderItem> findByIdAndOrderId(Long itemId, Long orderId);

    Set<OrderItem> findAllByOrderId(Long orderId);
}
