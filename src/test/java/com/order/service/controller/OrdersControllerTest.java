package com.order.service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.With;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.service.client.notify.EmailClient;
import com.order.service.client.pharmacy.ItemClient;
import com.order.service.client.pharmacy.StoreClient;
import com.order.service.dto.*;
import com.order.service.dtoexternal.EmailDto;
import com.order.service.model.Cart;
import com.order.service.service.CartService;
import com.order.service.service.OrdersService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

//@ContextConfiguration(classes = {OrdersController.class})
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class OrdersControllerTest {
    @Autowired
    private OrdersController ordersController;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private OrdersService ordersService;
    
    @MockBean
    private StoreClient storeClient;
    
    @MockBean
    private CartService cartService;
    
    @MockBean
    private EmailClient emailClient;
    
    @MockBean
    private ModelMapper modelMapper;


    OrdersDto mockOrdersDto;

    @BeforeEach
    public void setup() throws Exception{

        Set<ItemDto> items = new HashSet<>();
        mockOrdersDto = new OrdersDto();
        mockOrdersDto.setOrderId(1L);
        mockOrdersDto.setUserId(1L);
        mockOrdersDto.setOrderAddress(new AddressDto(1L,"Street","City","State","123123","India"));
        mockOrdersDto.setPrice(100.00);
        mockOrdersDto.setOptionalOrderDetails(true);
        mockOrdersDto.setOrderDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2021-08-08 12:15:00"));
        mockOrdersDto.setOrderDetails(new OrderDetailsDto(1L));
        mockOrdersDto.setQuantity(100L);
        mockOrdersDto.setItems(items);

    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void getOrder() throws Exception{
        when(ordersService.getOrder(anyLong())).thenReturn(mockOrdersDto);
       
        mockMvc.perform(MockMvcRequestBuilders.get("/order/order/get-order-details/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(1L));
        
    }

    @Test
    void setOrderDetails() throws Exception{
    	CartDto cartDto = new CartDto();
        cartDto.setCartId(123L);
        cartDto.setItems(new HashSet<>());
        cartDto.setPrice(10.0d);
        cartDto.setQuantity(1L);
    	when(cartService.getIfExistsElseCreate(1L)).thenReturn(new Cart());
    	 when(modelMapper.map((Object) any(), (Class<Object>) any())).thenReturn(cartDto);
    	 when(storeClient.checkout(cartDto)).thenReturn( new ResponseEntity<>(cartDto,HttpStatus.CONTINUE));

    	 assertEquals(ordersController.setOrderDetails(1L, mockOrdersDto).getStatusCodeValue(), 201);
    }
    
    @Test
    void setOrderDetails1() throws Exception{
    	mockOrdersDto.setOptionalOrderDetails(false);
    	CartDto cartDto = new CartDto();
        cartDto.setCartId(123L);
        cartDto.setItems(new HashSet<>());
        cartDto.setPrice(10.0d);
        cartDto.setQuantity(1L);
    	when(cartService.getIfExistsElseCreate(1L)).thenReturn(new Cart());
    	 when(modelMapper.map((Object) any(), (Class<Object>) any())).thenReturn(cartDto);
    	 when(storeClient.checkout(cartDto)).thenReturn( new ResponseEntity<>(cartDto,HttpStatus.CONTINUE));

    	 assertEquals(ordersController.setOrderDetails(1L, mockOrdersDto).getStatusCodeValue(), 201);
    }

    @Test

    void placeOrder() throws Exception{
        when(ordersService.createOrder(any(OrdersDto.class))).thenReturn(mockOrdersDto);
        when(storeClient.buyCart(new CartDto())).thenReturn( new ResponseEntity<>(new CartDto(),HttpStatus.CONTINUE));
        when(emailClient.sendBulkEmail((String) any(), (List<EmailDto>) any()))
        .thenReturn(new ResponseEntity<>(HttpStatus.CONTINUE));
        
        assertEquals(ordersController.placeOrder(mockOrdersDto).getStatusCodeValue(), 201);

    }




    @Test
    void testDeleteOrder() throws Exception {
        doNothing().when(ordersService).deleteOrder((Long) any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/order/order/delete-order/{id}",
                123L);
        MockMvcBuilders.standaloneSetup(ordersController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("Order Deleted Successfully!"));
    }

    @Test
    void testDeleteOrder2() throws Exception {
        doNothing().when(ordersService).deleteOrder((Long) any());
        MockHttpServletRequestBuilder deleteResult = MockMvcRequestBuilders.delete("/order/order/delete-order/{id}",
                123L);
        deleteResult.characterEncoding("Encoding");
        MockMvcBuilders.standaloneSetup(ordersController)
                .build()
                .perform(deleteResult)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("Order Deleted Successfully!"));
    }
    @Test
    void testDeleteOrder3() throws Exception {
        doNothing().when(ordersService).deleteAllOrder((List<Long>) any());
        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.delete("/order/order/delete-order")
                .contentType(MediaType.APPLICATION_JSON);

        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(new ArrayList<>()));
        MockMvcBuilders.standaloneSetup(ordersController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("Orders Deleted Successfully!"));
    }

    @Test
    void testDeleteOrderHistory() throws Exception {
        doNothing().when(ordersService).deleteAllOrder((Long) any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/order/order/delete-order-history/{id}", 123L);
        MockMvcBuilders.standaloneSetup(ordersController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("User Order History Cleared!"));
    }

    @Test
    void testDeleteOrderHistory2() throws Exception {
        doNothing().when(ordersService).deleteAllOrder((Long) any());
        MockHttpServletRequestBuilder deleteResult = MockMvcRequestBuilders
                .delete("/order/order/delete-order-history/{id}", 123L);
        deleteResult.characterEncoding("Encoding");
        MockMvcBuilders.standaloneSetup(ordersController)
                .build()
                .perform(deleteResult)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("User Order History Cleared!"));
    }

    @Test
    void testGetOrderHistory() throws Exception {
        when(ordersService.getOrders((Long) any(), (Integer) any(), (Integer) any()))
                .thenReturn(new PageableResponse<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/order/order/get-order-history/{id}",
                123L);
        MockMvcBuilders.standaloneSetup(ordersController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "{\"data\":null,\"pageNumber\":null,\"pageSize\":null,\"totalRecords\":null,\"totalPages\":null,\"isLastPage"
                                        + "\":null}"));
    }
}

