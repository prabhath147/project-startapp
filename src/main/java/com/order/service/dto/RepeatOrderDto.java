package com.order.service.dto;


import lombok.*;

import javax.validation.constraints.Min;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RepeatOrderDto {
    private Long id;
    private Long userId;
    private String name;
    @Min(value = 2, message = "Number of Deliveries should not be less than 2")
    private int numberOfDeliveries;
    @Min(value = 3, message = "Interval should not be less than 3 days")
    private int intervalInDays;
    private LocalDate deliveryDate;
    private AddressDto address;
    private Set<ProductDto> repeatOrderItems= new HashSet<>();
}
