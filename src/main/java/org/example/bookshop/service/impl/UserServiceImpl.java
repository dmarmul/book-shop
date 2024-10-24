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
import org.example.bookshop.service.CartService;
import org.example.bookshop.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final CartService cartService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException(
                    "User " + requestDto.getEmail() + " already exist");
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(roleRepository.findByRole(Role.RoleType.ROLE_USER)));
        userRepository.save(user);
        cartService.createShoppingCart(user);
        return userMapper.toDto(user);
    }
}
