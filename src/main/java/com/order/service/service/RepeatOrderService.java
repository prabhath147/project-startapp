package com.order.service.service;

import java.time.LocalDate;
import java.util.List;

import com.order.service.dto.ItemDto;
import com.order.service.dto.OrdersDto;
import com.order.service.dto.PageableResponse;
import com.order.service.dto.ProductDto;
import com.order.service.dto.RepeatOrderDto;
import com.order.service.dto.UserOptInDto;

import org.springframework.data.domain.Page;


public interface RepeatOrderService {
    PageableResponse<RepeatOrderDto> getAllOptInByUserId(Long userId,Integer pageNumber,Integer PageSize);

    RepeatOrderDto getOptInById(Long optinId);

    RepeatOrderDto createOptIn(RepeatOrderDto repeatOrderRequest);
    
    RepeatOrderDto updateOptIn(RepeatOrderDto repeatOrderRequest);
    
    List<ItemDto> getItemByProductId(String token, ProductDto productDto);
        
    void getOptInToSendNotification(LocalDate date);
    
    void deleteOptIn(Long optinId);
    
    void deleteAllOptInByUserId(Long userId);
    
}
