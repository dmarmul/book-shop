package org.example.bookshop.service.impl;

import java.util.Optional;
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
        Optional<User> user = userRepository.findByEmail(requestDto.getEmail());
        if (user.isEmpty()) {
            User saveUser = userMapper.toModel(requestDto);
            return userMapper.toDto(userRepository.save(saveUser));
        }
        throw new RegistrationException("User already exist");
    }
}
