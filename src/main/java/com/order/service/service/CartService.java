package com.order.service.service;

import com.order.service.dto.CartDto;
import com.order.service.dto.ItemDto;
import com.order.service.model.Cart;

import java.util.List;


public interface CartService {

    CartDto getCart(Long userId);
    CartDto addToCart(Long userId, ItemDto itemDto);
    CartDto checkout(Long userId, CartDto cartDto);
    CartDto addToCart(Long userId, List<ItemDto> itemDtoList);
    CartDto removeFromCart(Long userId, ItemDto itemDto);
    CartDto emptyCart(Long userId);
    Cart getIfExistsElseCreate(Long userId);

    CartDto reduceItemQuantity(Long userId, ItemDto itemDto);
    void deleteCart(Long userId);

}
