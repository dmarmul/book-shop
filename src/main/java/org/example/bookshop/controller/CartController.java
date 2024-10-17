package org.example.bookshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bookshop.dto.CartDto;
import org.example.bookshop.dto.CartItemDto;
import org.example.bookshop.dto.CartUpdateRequestDto;
import org.example.bookshop.dto.CreateCartRequestDto;
import org.example.bookshop.security.JwtUtil;
import org.example.bookshop.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Cart management", description = "Endpoints for managing carts")
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class CartController {
    private static final int BEARER_SUBSTRING = 7;

    private final CartService cartService;
    private final JwtUtil jwtUtil;
    private String userName;

    @ModelAttribute
    public void setUserName(@RequestHeader("Authorization") String token) {
        this.userName = jwtUtil.getUsername(token.substring(BEARER_SUBSTRING));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get shopping cart",
            description = "Retrieve user's shopping cart")
    public CartDto getAll() {
        return cartService.findAll(userName);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Add books to cart",
            description = "Add books to the shopping cart."
                    + " Fields bookId and quantity can't must be not less than 1, can't be null.")
    public CartItemDto add(@RequestBody @Valid CreateCartRequestDto requestDto) {
        return cartService.add(requestDto, userName);
    }

    @PutMapping("/items/{cartItemId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update the books quantity",
            description = "Update the books quantity in shopping cart")
    public CartItemDto update(@RequestBody @Valid CartUpdateRequestDto requestDto,
                              @PathVariable Long cartItemId) {
        return cartService.update(requestDto, cartItemId, userName);
    }

    @DeleteMapping("/items/{cartItemId}")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete cart item", description = "Delete cart items from shopping cart")
    public void delete(@PathVariable Long cartItemId) {
        cartService.delete(cartItemId, userName);
    }
}
