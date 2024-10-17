package org.example.bookshop.mapper;

import org.example.bookshop.config.MapperConfig;
import org.example.bookshop.dto.CartItemDto;
import org.example.bookshop.dto.CreateCartRequestDto;
import org.example.bookshop.model.CartItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    @Mapping(target = "bookId", ignore = true)
    @Mapping(target = "bookTitle", ignore = true)
    CartItemDto toCartItemDto(CartItem cartItem);

    CartItem toCartItem(CreateCartRequestDto createCartRequestDto);

    @AfterMapping
    default void setBookDetails(@MappingTarget CartItemDto cartItemDto, CartItem cartItem) {
        if (cartItem.getBook() != null) {
            cartItemDto.setBookId(cartItem.getBook().getId());
            cartItemDto.setBookTitle(cartItem.getBook().getTitle());
        }
    }
}
