package com.order.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.service.dto.AddressDto;
import com.order.service.dto.UserDetailsDto;
import com.order.service.service.UserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserDetailsControllerTest {
    @Autowired
    UserDetailsController userDetailsController;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private UserDetailsService userDetailsService;

    UserDetailsDto mockUserDetails;


    @BeforeEach
    public void setup() throws Exception{
        mockUserDetails = new UserDetailsDto();
        mockUserDetails.setUserId(1L);
        mockUserDetails.setFullName("User1");
        mockUserDetails.setMobileNumber("1234567890");
        mockUserDetails.setAddress(new AddressDto(1L,"Street","City","State","123123","India"));

    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void getUserDetailsById() throws Exception {
        when(userDetailsService.getUserDetails(anyLong())).thenReturn(mockUserDetails);

        mockMvc.perform(MockMvcRequestBuilders.get("/order/user-details/get-user-details/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(1L));
    }

    @Test
    @WithMockUser(username = "user", roles = {"ADMIN"})
    void createUserDetails() throws Exception{
        when(userDetailsService.createUserDetails(anyLong(),any(UserDetailsDto.class))).thenReturn(mockUserDetails);

        mockMvc.perform(MockMvcRequestBuilders.post("/order/user-details/create-user-details/1")
                        .content(new ObjectMapper().writeValueAsString(mockUserDetails)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(1L));
    }
}
