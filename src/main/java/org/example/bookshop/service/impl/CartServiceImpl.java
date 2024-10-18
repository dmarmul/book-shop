package org.example.bookshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.bookshop.dto.CartItemDto;
import org.example.bookshop.dto.CartItemRequestDto;
import org.example.bookshop.dto.CartUpdateRequestDto;
import org.example.bookshop.dto.ShoppingCartDto;
import org.example.bookshop.exception.EntityNotFoundException;
import org.example.bookshop.mapper.CartItemMapper;
import org.example.bookshop.mapper.ShoppingCartMapper;
import org.example.bookshop.model.Book;
import org.example.bookshop.model.CartItem;
import org.example.bookshop.model.ShoppingCart;
import org.example.bookshop.model.User;
import org.example.bookshop.repository.BookRepository;
import org.example.bookshop.repository.CartItemRepository;
import org.example.bookshop.repository.CartRepository;
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

    @Override
    public ShoppingCartDto get(User user) {
        ShoppingCart shoppingCart = findShoppingCart(user);
        return cartMapper.toDto(shoppingCart);
    }

    @Override
    public CartItemDto add(CartItemRequestDto requestDto, User user) {
        CartItem cartItem = cartItemMapper.toCartItem(requestDto);
        ShoppingCart shoppingCart = findShoppingCart(user);
        cartItem.setShoppingCart(shoppingCart);

        Book book = bookRepository.findById(requestDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Book not found with id: " + requestDto.getBookId()));
        cartItem.setBook(book);

        return cartItemMapper.toCartItemDto(cartItemRepository.save(cartItem));
    }

    @Override
    public CartItemDto update(CartUpdateRequestDto requestDto, Long id, User user) {
        CartItem cartItem = findCartItemById(findShoppingCart(user), id);
        cartItem.setQuantity(requestDto.getQuantity());

        return cartItemMapper.toCartItemDto(cartItemRepository.save(cartItem));
    }

    @Override
    public void delete(Long id, User user) {
        if (findCartItemById(findShoppingCart(user), id) != null) {
            cartItemRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Can't find cartItem by id: " + id);
        }
    }

    private ShoppingCart findShoppingCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                "Can't found cartRepository for user :" + user.getEmail())
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
