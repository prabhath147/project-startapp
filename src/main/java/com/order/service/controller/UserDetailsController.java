package com.order.service.controller;

import com.order.service.dto.UserDetailsDto;
import com.order.service.service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order/user-details")
public class UserDetailsController {
    @Autowired
    private UserDetailsService userDetailsService;

    @GetMapping(value = "/get-user-details/{id}")
    public ResponseEntity<UserDetailsDto> getUserDetails(@PathVariable("id") Long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(userDetailsService.getUserDetails(userId));
    }

    @PostMapping(value = "/create-user-details/{id}")
    @CrossOrigin("${authServiceURL}")
    public ResponseEntity<UserDetailsDto> createUserDetails(@PathVariable("id") Long userId, @RequestBody UserDetailsDto userDetailsDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userDetailsService.createUserDetails(userId, userDetailsDto));
    }
}
