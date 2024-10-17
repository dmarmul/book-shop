package org.example.bookshop.mapper;

import java.util.stream.Collectors;
import org.example.bookshop.config.MapperConfig;
import org.example.bookshop.dto.CartDto;
import org.example.bookshop.mapper.impl.CartItemMapperImpl;
import org.example.bookshop.model.ShoppingCart;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {
    @Mapping(target = "cartItems", ignore = true)
    CartDto toDto(ShoppingCart cart);

    @AfterMapping
    default void setCartDtoParam(@MappingTarget CartDto cartDto, ShoppingCart cart) {
        CartItemMapper cartItemMapper = new CartItemMapperImpl();
        cartDto.setUserId(cart.getUser().getId());
        cartDto.setCartItems(cart.getCartItems().stream()
                .map(cartItemMapper::toCartItemDto)
                .collect(Collectors.toSet()));
    }
}
