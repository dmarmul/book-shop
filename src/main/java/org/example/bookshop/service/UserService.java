package org.example.bookshop.service;

import org.example.bookshop.dto.UserRegistrationRequestDto;
import org.example.bookshop.dto.UserResponseDto;

public interface UserService {
    UserResponseDto save(UserRegistrationRequestDto requestDto);
}
