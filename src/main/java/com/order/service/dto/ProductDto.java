package com.order.service.dto;

import lombok.*;
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long productId;

    private Long productIdFk;

    private Long quantity;

    private Double price;

}
