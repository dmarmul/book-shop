package org.example.bookshop.dto;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartDto {
    private Long id;
    private Long userId;
    private Set<CartItemDto> cartItems;
}
