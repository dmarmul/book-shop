package org.example.bookshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.bookshop.dto.UserRegistrationRequestDto;
import org.example.bookshop.dto.UserResponseDto;
import org.example.bookshop.exception.RegistrationException;
import org.example.bookshop.mapper.UserMapper;
import org.example.bookshop.model.User;
import org.example.bookshop.repository.UserRepository;
import org.example.bookshop.service.UserService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto save(UserRegistrationRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("User already exist");
        }

        User user = userMapper.toModel(requestDto);
        User saved = userRepository.save(user);
        UserResponseDto dto = userMapper.toDto(saved);
        System.out.println(dto);
        return dto;
    }
}
