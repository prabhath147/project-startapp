package com.order.service.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.service.dto.CartDto;
import com.order.service.dto.ItemDto;
import com.order.service.service.CartService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {CartController.class})
@ExtendWith(SpringExtension.class)
class CartControllerTest {
    @Autowired
    private CartController cartController;

    @MockBean
    private CartService cartService;

    @Test
    void testGetCart() throws Exception {
        when(cartService.getCart((Long) any())).thenReturn(new CartDto());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/order/cart/get-cart/{id}", 123L);
        MockMvcBuilders.standaloneSetup(cartController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string("{\"cartId\":null,\"items\":[],\"quantity\":null,\"price\":null}"));
    }

    @Test
    void testAddPrescriptionToCart() throws Exception {
        when(cartService.addToCart((Long) any(), (List<ItemDto>) any())).thenReturn(new CartDto());
        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders
                .post("/order/cart/add-prescription-to-cart/{id}", 123L)
                .contentType(MediaType.APPLICATION_JSON);

        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult
                .content(objectMapper.writeValueAsString(new ArrayList<>()));
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(cartController).build().perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(
                        MockMvcResultMatchers.content().string("{\"cartId\":null,\"items\":[],\"quantity\":null,\"price\":null}"));
    }

    @Test
    void testAddToCart() throws Exception {
        when(cartService.addToCart((Long) any(), (ItemDto) any())).thenReturn(new CartDto());

        ItemDto itemDto = new ItemDto();
        itemDto.setItemId(123L);
        itemDto.setItemIdFk(42L);
        itemDto.setItemQuantity(42L);
        itemDto.setPrice(10.0d);
       // itemDto.setItemQuantity(1);
        String content = (new ObjectMapper()).writeValueAsString(itemDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/order/cart/add-to-cart/{id}", 123L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(cartController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string("{\"cartId\":null,\"items\":[],\"quantity\":null,\"price\":null}"));
    }

    @Test
    void testEmptyCart() throws Exception {
        when(cartService.emptyCart((Long) any())).thenReturn(new CartDto());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/order/cart/empty-cart/{id}", 123L);
        MockMvcBuilders.standaloneSetup(cartController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string("{\"cartId\":null,\"items\":[],\"quantity\":null,\"price\":null}"));
    }

    @Test
    void testDeleteItemFromCart() throws Exception {
        when(cartService.removeFromCart((Long) any(), (ItemDto) any())).thenReturn(new CartDto());

        ItemDto itemDto = new ItemDto();
        itemDto.setItemId(123L);
        itemDto.setItemIdFk(42L);
        itemDto.setItemQuantity(42L);
        itemDto.setPrice(10.0d);
       // itemDto.setQuantity(1);
        String content = (new ObjectMapper()).writeValueAsString(itemDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put("/order/cart/remove-from-cart/{id}", 123L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        MockMvcBuilders.standaloneSetup(cartController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string("{\"cartId\":null,\"items\":[],\"quantity\":null,\"price\":null}"));
    }

    @Test
    void testPlaceOrder() throws Exception {
        when(cartService.checkout((Long) any(), (CartDto) any())).thenReturn(new CartDto());

        CartDto cartDto = new CartDto();
        cartDto.setCartId(123L);
        cartDto.setItems(new HashSet<>());
        cartDto.setPrice(10.0d);
        cartDto.setQuantity(1L);
        String content = (new ObjectMapper()).writeValueAsString(cartDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/order/cart/checkout/{id}", 123L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(cartController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string("{\"cartId\":null,\"items\":[],\"quantity\":null,\"price\":null}"));
    }
}