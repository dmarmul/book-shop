package org.example.bookshop.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserResponseDto {
    private Long id;
    private String email;
    private String password;
    private String firstName;
    private BigDecimal lastName;
    private String shippingAddress;
}
