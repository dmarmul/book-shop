package org.example.bookshop.repository;

import java.util.Optional;
import org.example.bookshop.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = {"user", "orderItems"})
    Page<Order> findAllByUserId(Long id, @NonNull Pageable pageable);

    @EntityGraph(attributePaths = {"user", "orderItems"})
    Optional<Order> findByIdAndUserId(Long orderId, Long userId);
}
