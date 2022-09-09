package com.order.service.controller;

import com.order.service.dto.OrdersDto;
import com.order.service.dto.PageableResponse;
import com.order.service.dto.RepeatOrderDto;


import com.order.service.service.RepeatOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;

@RestController
@RequestMapping("/order")
public class RepeatOrderController {

    @Autowired
    private RepeatOrderService repeatOrderService;

    @GetMapping("/optin/{id}")
    public ResponseEntity<RepeatOrderDto> getOptInById(@PathVariable("id") Long optinId) {
        return ResponseEntity.status(HttpStatus.OK).body(repeatOrderService.getOptInById(optinId));
    }
    @GetMapping("/optin/user/{id}")
    public ResponseEntity<PageableResponse<RepeatOrderDto>> getOptInByUserId(@PathVariable("id") Long userId,
    		@RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize) {
        return ResponseEntity.status(HttpStatus.OK).body(repeatOrderService.getAllOptInByUserId(userId,pageNumber,pageSize));
    }
    @PostMapping("/optin/create-optin")
    public ResponseEntity<RepeatOrderDto> createOptIn(@Valid @RequestBody RepeatOrderDto repeatOrderRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repeatOrderService.createOptIn(repeatOrderRequest));
    }
    
    @PutMapping("/optin/update-optin")
    public ResponseEntity<RepeatOrderDto> updateOptIn(@Valid @RequestBody RepeatOrderDto repeatOrderRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(repeatOrderService.updateOptIn(repeatOrderRequest));
    }
    

    @DeleteMapping(value = "optin/delete/{id}")
    public ResponseEntity<String> deleteOptInById(@PathVariable("id") Long optinId) {
        repeatOrderService.deleteOptIn(optinId);
        return ResponseEntity.status(HttpStatus.OK).body("Optin Order Deleted Successfully!");
    }
    
    @DeleteMapping(value = "optin/delete-optin-history/{id}")
    public ResponseEntity<String> deleteOptInHistory(@PathVariable("id") Long userId) {
        repeatOrderService.deleteAllOptInByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body("User Optin History Deleted Successfully!");
    }
    

}
