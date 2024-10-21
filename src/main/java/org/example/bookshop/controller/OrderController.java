package org.example.bookshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.example.bookshop.dto.OrderDto;
import org.example.bookshop.dto.OrderItemDto;
import org.example.bookshop.dto.OrderPurchaseRequestDto;
import org.example.bookshop.dto.OrderStatusRequestDto;
import org.example.bookshop.model.User;
import org.example.bookshop.service.OrderService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoints for managing orders")
@RequiredArgsConstructor
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Place order",
            description = "Places orders in the shopping cart."
                    + " ShippingAddress can't be empty.")
    public OrderDto add(@RequestBody @Valid OrderPurchaseRequestDto requestDto,
                        @AuthenticationPrincipal User user) {
        return orderService.add(requestDto, user);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get orders history",
            description = "Retrieve all user's orders")
    public Set<OrderDto> getAll(@AuthenticationPrincipal User user,
                                Sort sort, Pageable pageable) {
        return orderService.getAll(user, sort, pageable);
    }

    @PatchMapping("/{orderId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update order status",
            description = "Update order status")
    public void update(@RequestBody @Valid OrderStatusRequestDto requestDto,
                       @PathVariable Long orderId,
                       @AuthenticationPrincipal User user) {
        orderService.update(requestDto, orderId, user);
    }

    @GetMapping("/{orderId}/items")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get OrderItems",
            description = "Retrieve all OrderItems for a specific order")
    public Set<OrderItemDto> get(@PathVariable Long orderId,
                                 @AuthenticationPrincipal User user) {
        return orderService.get(orderId, user);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get OrderItem",
            description = "Retrieve a specific OrderItem within an order")
    public OrderItemDto getOrderItem(@PathVariable Long orderId,
                                @PathVariable Long itemId,
                                @AuthenticationPrincipal User user) {
        return orderService.getOrderItem(orderId, itemId, user);
    }
}
