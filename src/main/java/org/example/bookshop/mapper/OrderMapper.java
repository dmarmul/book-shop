package org.example.bookshop.mapper;

import org.example.bookshop.config.MapperConfig;
import org.example.bookshop.dto.OrderDto;
import org.example.bookshop.model.Order;
import org.example.bookshop.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class, uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(source = "user.id", target = "userId")
    OrderDto toDto(Order order);

    @Mapping(target = "id", ignore = true)
    Order toEntity(ShoppingCart shoppingCart);
}
