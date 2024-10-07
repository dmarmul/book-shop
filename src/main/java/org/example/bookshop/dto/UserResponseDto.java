package org.example.bookshop.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserResponseDto {
    private String email;
    private String firstName;
    private String lastName;
}