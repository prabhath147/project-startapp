package com.order.service.controller;

import com.order.service.dto.CartDto;
import com.order.service.dto.ItemDto;
import com.order.service.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/order/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping(value = "/get-cart/{id}")
    public ResponseEntity<CartDto> getCart(@PathVariable("id") Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(cartService.getCart(userId));
    }

    @PostMapping("/checkout/{id}")
    public ResponseEntity<CartDto> placeOrder(@PathVariable("id") Long userId, @RequestBody CartDto cartDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.checkout(userId, cartDto));
    }

    @PostMapping("/add-to-cart/{id}")
    public ResponseEntity<CartDto> addToCart(@PathVariable("id") Long userId, @RequestBody ItemDto itemDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addToCart(userId, itemDto));
    }

    @PostMapping("/add-prescription-to-cart/{id}")
    public ResponseEntity<CartDto> addPrescriptionToCart(@PathVariable("id") Long userId, @RequestBody List<ItemDto> itemDtoList) {

        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addToCart(userId, itemDtoList));
    }

    @PutMapping("/remove-from-cart/{id}")
    public ResponseEntity<CartDto> deleteItemFromCart(@PathVariable("id") Long userId, @RequestBody ItemDto itemDto) {
        return ResponseEntity.status(HttpStatus.OK).body(cartService.removeFromCart(userId, itemDto));
    }

    @DeleteMapping("/empty-cart/{id}")
    public ResponseEntity<CartDto> emptyCart(@PathVariable("id") Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(cartService.emptyCart(userId));
    }

}
