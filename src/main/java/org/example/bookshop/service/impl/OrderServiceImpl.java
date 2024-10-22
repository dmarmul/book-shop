package org.example.bookshop.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.bookshop.dto.OrderDto;
import org.example.bookshop.dto.OrderItemDto;
import org.example.bookshop.dto.OrderPurchaseRequestDto;
import org.example.bookshop.dto.OrderStatusRequestDto;
import org.example.bookshop.exception.DataProcessingException;
import org.example.bookshop.exception.EntityNotFoundException;
import org.example.bookshop.mapper.OrderItemMapper;
import org.example.bookshop.mapper.OrderMapper;
import org.example.bookshop.model.Order;
import org.example.bookshop.model.OrderItem;
import org.example.bookshop.model.ShoppingCart;
import org.example.bookshop.model.User;
import org.example.bookshop.repository.CartItemRepository;
import org.example.bookshop.repository.CartRepository;
import org.example.bookshop.repository.OrderItemRepository;
import org.example.bookshop.repository.OrderRepository;
import org.example.bookshop.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderDto add(OrderPurchaseRequestDto requestDto, User user) {
        ShoppingCart shoppingCart = cartRepository.findByUserId(
                user.getId()).orElseThrow(() -> new EntityNotFoundException(
                "Can't find order for user: " + user.getEmail()));
        Order order = createOrder(shoppingCart, requestDto, user);
        shoppingCart.getCartItems().clear();
        cartRepository.save(shoppingCart);
        return orderMapper.toDto(order);
    }

    @Override
    public Page<OrderDto> getAll(User user, Sort sort, Pageable pageable) {
        return orderRepository.findAllByUserId(user.getId(), pageable)
                .map(orderMapper::toDto);
    }

    @Override
    public void update(OrderStatusRequestDto requestDto, Long orderId, User user) {
        Order order = findOrderByIdAndUserId(orderId, user.getId());
        Order.Status status = Order.Status.valueOf(requestDto.getStatus());
        if (order.getStatus().equals(status)) {
            throw new DataProcessingException("Order already has status: " + status);
        }
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Override
    public Set<OrderItemDto> get(Long orderId, User user) {
        findOrderByIdAndUserId(orderId, user.getId());
        return orderItemRepository.findAllByOrderId(orderId).stream()
                .map(orderItemMapper::toDto)
                .collect(Collectors.toSet());
    }

    @Override
    public OrderItemDto getOrderItem(Long orderId, Long itemId, User user) {
        return orderItemMapper.toDto(
                orderItemRepository.findByIdAndOrderId(itemId, orderId)
                        .filter(orderItem ->
                                orderItem.getOrder().getUser().getId().equals(user.getId()))
                        .orElseThrow(() ->
                                new EntityNotFoundException("Can't find item with id: " + itemId))
        );
    }

    private Order createOrder(ShoppingCart shoppingCart,
                              OrderPurchaseRequestDto requestDto, User user) {
        Order order = orderMapper.toEntity(shoppingCart);
        order.setShippingAddress(requestDto.getShippingAddress());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.Status.PENDING);
        Set<OrderItem> orderItems = findOrderItems(user);
        orderItems.forEach(item ->
                item.setPrice(item.getPrice().multiply(new BigDecimal(item.getQuantity()))));
        order.setTotal(getTotalPrice(orderItems));
        orderRepository.save(order);
        orderItems.forEach(item -> {
            item.setOrder(order);
            orderItemRepository.save(item);
        });
        order.setOrderItems(orderItems);
        return order;
    }

    private Order findOrderByIdAndUserId(Long orderId, Long userId) {
        return orderRepository.findByIdAndUserId(orderId, userId).orElseThrow(() ->
                new EntityNotFoundException("Can't find order with id: " + orderId)
        );
    }

    private Set<OrderItem> findOrderItems(User user) {
        Set<OrderItem> orderItem = cartItemRepository.findAllByShoppingCartId(
                cartRepository.findByUserId(user.getId()).get().getId())
                .stream()
                .map(orderItemMapper::toEntity)
                .collect(Collectors.toSet());
        if (orderItem.isEmpty()) {
            throw new EntityNotFoundException(
                    "Can't find order items for user: " + user.getEmail()
                            + " Can't place an empty order");
        }
        return orderItem;
    }

    private BigDecimal getTotalPrice(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
