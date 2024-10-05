package org.example.bookshop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.example.bookshop.annotation.FieldMatch;

@Getter
@Setter
@FieldMatch(first = "password", second = "confirmPassword")
public class UserRegistrationRequestDto {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min = 6, max = 30)
    private String password;
    @NotBlank
    @Size(min = 6, max = 30)
    private String confirmPassword;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String shippingAddress;
}
