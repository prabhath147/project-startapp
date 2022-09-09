package com.order.service.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.service.dto.AddressDto;
import com.order.service.dto.PageableResponse;
import com.order.service.dto.RepeatOrderDto;
import com.order.service.service.RepeatOrderService;

import java.util.HashSet;

import org.junit.jupiter.api.Disabled;
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

@ContextConfiguration(classes = {RepeatOrderController.class})
@ExtendWith(SpringExtension.class)
class RepeatOrderControllerTest {
    @Autowired
    private RepeatOrderController repeatOrderController;

    @MockBean
    private RepeatOrderService repeatOrderService;
    @Test
    void testUpdateOptIn() throws Exception {
        when(repeatOrderService.updateOptIn((RepeatOrderDto) any())).thenReturn(new RepeatOrderDto());

        RepeatOrderDto repeatOrderDto = new RepeatOrderDto();
        repeatOrderDto.setAddress(new AddressDto());
        repeatOrderDto.setDeliveryDate(null);
        repeatOrderDto.setId(123L);
        repeatOrderDto.setIntervalInDays(42);
        repeatOrderDto.setName("Name");
        repeatOrderDto.setNumberOfDeliveries(10);
        repeatOrderDto.setRepeatOrderItems(new HashSet<>());
        repeatOrderDto.setUserId(123L);
        String content = (new ObjectMapper()).writeValueAsString(repeatOrderDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/order/optin/update-optin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        MockMvcBuilders.standaloneSetup(repeatOrderController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "{\"id\":null,\"userId\":null,\"name\":null,\"numberOfDeliveries\":0,\"intervalInDays\":0,\"deliveryDate\":null,"
                                        + "\"address\":null,\"repeatOrderItems\":[]}"));
    }
    
    @Test
    void testCreateOptIn() throws Exception {
        when(repeatOrderService.createOptIn((RepeatOrderDto) any())).thenReturn(new RepeatOrderDto());
        RepeatOrderDto repeatOrderDto = new RepeatOrderDto();
        repeatOrderDto.setAddress(new AddressDto());
        repeatOrderDto.setDeliveryDate(null);
        repeatOrderDto.setId(123L);
        repeatOrderDto.setIntervalInDays(42);
        repeatOrderDto.setName("Name");
        repeatOrderDto.setNumberOfDeliveries(10);
        repeatOrderDto.setRepeatOrderItems(new HashSet<>());
        repeatOrderDto.setUserId(123L);
        String content = (new ObjectMapper()).writeValueAsString(repeatOrderDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/order/optin/create-optin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        MockMvcBuilders.standaloneSetup(repeatOrderController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().is(201))
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "{\"id\":null,\"userId\":null,\"name\":null,\"numberOfDeliveries\":0,\"intervalInDays\":0,\"deliveryDate\":null,"
                                        + "\"address\":null,\"repeatOrderItems\":[]}"));
    }

    @Test
    void testGetOptInById() throws Exception {
        MockHttpServletRequestBuilder getResult = MockMvcRequestBuilders.get("/order/optin/{id}", 123L);
        MockHttpServletRequestBuilder requestBuilder = getResult.param("optinId", String.valueOf(1L));
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(repeatOrderController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(200));
    }
    @Test
    void testPlaceOrderByOptInId() throws Exception {
        MockHttpServletRequestBuilder getResult = MockMvcRequestBuilders.post("/order/optin/place-order/{id}", 123L);
        MockHttpServletRequestBuilder requestBuilder = getResult.param("userId", String.valueOf(1L));
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(repeatOrderController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(200));
    }

    @Test
    void testDeleteOptInById() throws Exception {
        doNothing().when(repeatOrderService).deleteOptIn((Long) any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/order/optin/delete/{id}", 123L);
        MockMvcBuilders.standaloneSetup(repeatOrderController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("Optin Order Deleted Successfully!"));
    }
    @Test
    void testGetOptInByUserId() throws Exception {
        when(repeatOrderService.getAllOptInByUserId((Long) any(), (Integer) any(), (Integer) any()))
                .thenReturn(new PageableResponse<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/order/optin/user/{id}", 123L);
        MockMvcBuilders.standaloneSetup(repeatOrderController)
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

