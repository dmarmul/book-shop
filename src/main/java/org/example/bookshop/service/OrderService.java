package org.example.bookshop.service;

import jakarta.validation.Valid;
import java.util.Set;
import org.example.bookshop.dto.OrderDto;
import org.example.bookshop.dto.OrderItemDto;
import org.example.bookshop.dto.OrderPurchaseRequestDto;
import org.example.bookshop.dto.OrderStatusRequestDto;
import org.example.bookshop.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface OrderService {
    OrderDto add(@Valid OrderPurchaseRequestDto requestDto, User user);

    Set<OrderDto> getAll(User user, Sort sort, Pageable pageable);

    void update(@Valid OrderStatusRequestDto requestDto, Long orderId, User user);

    Set<OrderItemDto> get(Long orderId, User user);

    OrderItemDto getOrderItem(Long orderId, Long itemId, User user);
}
