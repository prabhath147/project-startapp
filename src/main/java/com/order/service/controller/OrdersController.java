package com.order.service.controller;

import com.order.service.client.notify.EmailClient;
import com.order.service.client.pharmacy.StoreClient;
import com.order.service.dto.CartDto;
import com.order.service.dto.OrdersDto;
import com.order.service.dto.PageableResponse;
import com.order.service.dtoexternal.EmailDto;
import com.order.service.service.CartService;
import com.order.service.service.OrdersService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order/order")
public class OrdersController {
    @Autowired
    private CartService cartService;

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private StoreClient storeClient;

    @Autowired
    private EmailClient emailClient;

    @GetMapping(value = "/get-order-details/{id}")
    public ResponseEntity<OrdersDto> getOrder(@PathVariable("id") Long orderId) {
        return ResponseEntity.status(HttpStatus.OK).body(ordersService.getOrder(orderId));
    }

    @GetMapping(value = "/get-order-history/{id}")
    public ResponseEntity<PageableResponse<OrdersDto>> getOrderHistory(
            @PathVariable("id") Long userId,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(ordersService.getOrders(userId, pageNumber, pageSize));
    }

    @PostMapping(value = "/set-order-details/{id}")
    public ResponseEntity<OrdersDto> setOrderDetails(@PathVariable("id") Long userId, @RequestBody OrdersDto ordersDto) {
        if (!ordersDto.isOptionalOrderDetails()) {
            ordersDto.setOrderDetails(null);
        }
        OrdersDto ordersDto1 = new OrdersDto(userId, ordersDto.getOrderDetails(), ordersDto.getOrderAddress(), ordersDto.isOptionalOrderDetails());
        CartDto cartDto = modelMapper.map(cartService.getIfExistsElseCreate(userId), CartDto.class);
        cartDto = storeClient.checkout(cartDto).getBody();
        assert cartDto != null;
        ordersDto1.setItems(cartDto.getItems());
        ordersDto1.setPrice(cartDto.getPrice());
        ordersDto1.setQuantity(cartDto.getQuantity());

        return ResponseEntity.status(HttpStatus.CREATED).body(ordersDto1);
    }

    @PostMapping(value = "/place-order")
    public ResponseEntity<OrdersDto> placeOrder(@RequestBody OrdersDto ordersDto) {
        CartDto cartDto = new CartDto(ordersDto.getUserId(), ordersDto.getItems(), ordersDto.getQuantity(), ordersDto.getPrice());
        storeClient.buyCart(cartDto);

        List<EmailDto> emailDtoList = new ArrayList<>();
        EmailDto emailDto = new EmailDto(cartDto.getCartId(), "Order placed successfully!", "Order placed successfully!");
        emailDtoList.add(emailDto);
        emailClient.sendBulkEmail(emailDtoList);

        return ResponseEntity.status(HttpStatus.CREATED).body(ordersService.createOrder(ordersDto));
    }

    @DeleteMapping(value = "/delete-order/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable("id") Long orderId) {
        ordersService.deleteOrder(orderId);
        return ResponseEntity.status(HttpStatus.OK).body("Order Deleted Successfully!");
    }

    @DeleteMapping(value = "/delete-order-history/{id}")
    public ResponseEntity<String> deleteOrderHistory(@PathVariable("id") Long userId) {
        ordersService.deleteAllOrder(userId);
        return ResponseEntity.status(HttpStatus.OK).body("User Order History Cleared!");
    }

    @DeleteMapping(value = "/delete-order")
    public ResponseEntity<String> deleteOrder(@RequestBody List<Long> orderList) {
        ordersService.deleteAllOrder(orderList);
        return ResponseEntity.status(HttpStatus.OK).body("Orders Deleted Successfully!");
    }


}
