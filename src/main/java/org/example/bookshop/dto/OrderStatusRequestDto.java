package org.example.bookshop.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderStatusRequestDto {
    @NotBlank
    private String status;
}
