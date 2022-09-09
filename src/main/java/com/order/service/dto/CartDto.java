package com.order.service.dto;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CartDto {
    private Long cartId;
    private Set<ItemDto> items = new HashSet<>();
    private Long quantity;
    private Double price;
}
