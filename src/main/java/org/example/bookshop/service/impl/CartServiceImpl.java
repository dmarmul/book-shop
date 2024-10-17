package org.example.bookshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.bookshop.dto.CartDto;
import org.example.bookshop.dto.CartItemDto;
import org.example.bookshop.dto.CartUpdateRequestDto;
import org.example.bookshop.dto.CreateCartRequestDto;
import org.example.bookshop.exception.EntityNotFoundException;
import org.example.bookshop.mapper.CartItemMapper;
import org.example.bookshop.mapper.ShoppingCartMapper;
import org.example.bookshop.model.Book;
import org.example.bookshop.model.CartItem;
import org.example.bookshop.model.ShoppingCart;
import org.example.bookshop.repository.BookRepository;
import org.example.bookshop.repository.CartItemRepository;
import org.example.bookshop.repository.CartRepository;
import org.example.bookshop.repository.UserRepository;
import org.example.bookshop.service.CartService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService {
    private final BookRepository bookRepository;
    private final CartItemMapper cartItemMapper;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ShoppingCartMapper cartMapper;
    private final UserRepository userRepository;

    @Override
    public CartDto findAll(String username) {
        ShoppingCart shoppingCart = findShoppingCart(username);
        return cartMapper.toDto(shoppingCart);
    }

    @Override
    public CartItemDto add(CreateCartRequestDto requestDto, String username) {
        CartItem cartItem = cartItemMapper.toCartItem(requestDto);
        ShoppingCart shoppingCart = findShoppingCart(username);
        cartItem.setShoppingCart(shoppingCart);

        Book book = bookRepository.findById(requestDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Book not found with id: " + requestDto.getBookId()));
        cartItem.setBook(book);

        return cartItemMapper.toCartItemDto(cartItemRepository.save(cartItem));
    }

    @Override
    public CartItemDto update(CartUpdateRequestDto requestDto, Long id, String username) {
        CartItem cartItem = findCartItemById(findShoppingCart(username), id);
        cartItem.setQuantity(requestDto.getQuantity());

        return cartItemMapper.toCartItemDto(cartItemRepository.save(cartItem));
    }

    @Override
    public void delete(Long id, String username) {
        if (findCartItemById(findShoppingCart(username), id) != null) {
            cartItemRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Can't find cartItem by id: " + id);
        }
    }

    private Long findUserId(String username) {
        return userRepository.findByEmail(username).orElseThrow(
                () -> new EntityNotFoundException("Can't found user: " + username)
        ).getId();
    }

    private ShoppingCart findShoppingCart(String username) {
        return cartRepository.findByUserId(findUserId(username))
                .orElseThrow(() -> new EntityNotFoundException(
                "Can't found cartRepository for user :" + username)
        );
    }

    private CartItem findCartItemById(ShoppingCart shoppingCart, Long id) {
        return shoppingCart.getCartItems().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElseThrow(
                        () -> new EntityNotFoundException("Can't find cartItem by id: " + id)
                );
    }
}
