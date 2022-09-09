package com.order.service.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {

    private Long itemId;

    private Long itemIdFk;

    private Double price;
    
    private Long itemQuantity;

}
