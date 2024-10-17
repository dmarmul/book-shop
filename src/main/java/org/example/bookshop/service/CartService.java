package org.example.bookshop.service;

import jakarta.validation.Valid;
import org.example.bookshop.dto.CartDto;
import org.example.bookshop.dto.CartItemDto;
import org.example.bookshop.dto.CartUpdateRequestDto;
import org.example.bookshop.dto.CreateCartRequestDto;

public interface CartService {
    CartDto findAll(String username);

    CartItemDto add(@Valid CreateCartRequestDto requestDto, String username);

    CartItemDto update(@Valid CartUpdateRequestDto requestDto, Long cartItemId, String username);

    void delete(Long cartItemId, String username);
}
