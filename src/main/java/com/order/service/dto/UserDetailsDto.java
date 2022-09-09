package com.order.service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDetailsDto {
    private Long userId;
    private String fullName;
    private String mobileNumber;
    private AddressDto address;
}
