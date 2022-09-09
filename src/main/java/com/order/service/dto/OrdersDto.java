package com.order.service.dto;

import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class OrdersDto {

    private Long orderId;

    private Long userId;

    private Set<ItemDto> items = new HashSet<>();

    private Long quantity = 0L;

    private Double price = 0D;

    private OrderDetailsDto orderDetails;

    private AddressDto orderAddress;

    private Date orderDate = new Date();

    private boolean optionalOrderDetails;

    public OrdersDto(Long userId, OrderDetailsDto orderDetails, AddressDto orderAddress, boolean optionalOrderDetails) {
        this.userId = userId;
        this.orderDetails = orderDetails;
        this.orderAddress = orderAddress;
        this.optionalOrderDetails = optionalOrderDetails;
        this.orderDate = new Date();
        this.items = new HashSet<>();
        this.price = 0D;
        this.quantity = 0L;
    }
}
