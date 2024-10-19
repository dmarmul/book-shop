package org.example.bookshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bookshop.dto.CartItemRequestDto;
import org.example.bookshop.dto.CartUpdateRequestDto;
import org.example.bookshop.dto.ShoppingCartDto;
import org.example.bookshop.model.User;
import org.example.bookshop.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Cart management", description = "Endpoints for managing carts")
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get shopping cart",
            description = "Retrieve user's shopping cart")
    public ShoppingCartDto get(@AuthenticationPrincipal User user) {
        return cartService.get(user);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Add books to cart",
            description = "Add books to the shopping cart."
                    + " Fields bookId and quantity can't must be not less than 1, can't be null.")
    public ShoppingCartDto add(@RequestBody @Valid CartItemRequestDto requestDto,
                           @AuthenticationPrincipal User user) {
        return cartService.add(requestDto, user);
    }

    @PutMapping("/items/{cartItemId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update the books quantity",
            description = "Update the books quantity in shopping cart")
    public ShoppingCartDto update(@RequestBody @Valid CartUpdateRequestDto requestDto,
                                  @PathVariable Long cartItemId,
                                  @AuthenticationPrincipal User user) {
        return cartService.update(requestDto, cartItemId, user);
    }

    @DeleteMapping("/items/{cartItemId}")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete cart item", description = "Delete cart items from shopping cart")
    public void delete(@PathVariable Long cartItemId, @AuthenticationPrincipal User user) {
        cartService.delete(cartItemId, user);
    }
}
