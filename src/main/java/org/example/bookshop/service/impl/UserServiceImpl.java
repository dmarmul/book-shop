package org.example.bookshop.service.impl;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.example.bookshop.dto.UserRegistrationRequestDto;
import org.example.bookshop.dto.UserResponseDto;
import org.example.bookshop.exception.RegistrationException;
import org.example.bookshop.mapper.UserMapper;
import org.example.bookshop.model.Role;
import org.example.bookshop.model.User;
import org.example.bookshop.repository.RoleRepository;
import org.example.bookshop.repository.UserRepository;
import org.example.bookshop.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException(
                    "User " + requestDto.getEmail() + " already exist");
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles((Set.of(roleRepository.findByName(Role.Name.USER))));
        return userMapper.toDto(userRepository.save(user));
    }
}
