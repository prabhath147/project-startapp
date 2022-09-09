package com.order.service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AddressDto {
    private Long addressId;

    private String street;

    private String city;

    private String state;

    private String pinCode;

    private String country;
}
