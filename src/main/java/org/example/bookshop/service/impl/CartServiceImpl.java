package org.example.bookshop.service.impl;

import lombok.RequiredArgsConstructor;
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
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CartServiceImpl implements CartService {
    private final BookRepository bookRepository;
    private final CartItemMapper cartItemMapper;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    public ShoppingCartDto get(User user) {
        ShoppingCart shoppingCart = findShoppingCart(user);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Transactional
    @Override
    public ShoppingCartDto add(CartItemRequestDto requestDto, User user) {
        ShoppingCart shoppingCart = findShoppingCart(user);
        Book book = bookRepository.findById(requestDto.getBookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Book not found with id: " + requestDto.getBookId()));

        CartItem cartItem = getCartItemByBook(shoppingCart, requestDto);
        if (cartItem.getShoppingCart() == null) {
            cartItem.setBook(book);
            cartItem.setShoppingCart(shoppingCart);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + requestDto.getQuantity());
        }
        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toDto(cartRepository.save(shoppingCart));
    }

    @Transactional
    @Override
    public ShoppingCartDto update(CartUpdateRequestDto requestDto, Long id, User user) {
        ShoppingCart shoppingCart = findShoppingCart(user);
        CartItem cartItem = findCartItemById(shoppingCart, id);
        cartItem.setQuantity(requestDto.getQuantity());

        return shoppingCartMapper.toDto(cartRepository.save(shoppingCart));
    }

    @Override
    public void delete(Long id, User user) {
        if (cartItemRepository.findByIdAndUserId(id, user.getId()).isEmpty()) {
            throw new EntityNotFoundException("Can't find cartItem by id: " + id);
        }
        cartItemRepository.deleteById(id);
    }

    @Override
    public void createShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        cartRepository.save(shoppingCart);
    }

    private ShoppingCart findShoppingCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                "Can't find cartRepository for user :" + user.getEmail())
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

    private CartItem getCartItemByBook(ShoppingCart shoppingCart, CartItemRequestDto requestDto) {
        return shoppingCart.getCartItems().stream()
                .filter(item -> item.getShoppingCart().getId().equals(shoppingCart.getId())
                        && item.getBook().getId().equals(requestDto.getBookId()))
                .findFirst()
                .orElseGet(() -> cartItemMapper.toCartItem(requestDto));
    }
}
