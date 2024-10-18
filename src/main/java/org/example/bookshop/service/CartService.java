package org.example.bookshop.service;

import jakarta.validation.Valid;
import org.example.bookshop.dto.CartItemDto;
import org.example.bookshop.dto.CartItemRequestDto;
import org.example.bookshop.dto.CartUpdateRequestDto;
import org.example.bookshop.dto.ShoppingCartDto;
import org.example.bookshop.model.User;

public interface CartService {
    ShoppingCartDto get(User user);

    CartItemDto add(@Valid CartItemRequestDto requestDto, User user);

    CartItemDto update(@Valid CartUpdateRequestDto requestDto, Long cartItemId, User user);

    void delete(Long cartItemId, User user);
}
