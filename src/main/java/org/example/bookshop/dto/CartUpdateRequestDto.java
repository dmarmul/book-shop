package org.example.bookshop.dto;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartUpdateRequestDto {
    @Positive
    private int quantity;
}
