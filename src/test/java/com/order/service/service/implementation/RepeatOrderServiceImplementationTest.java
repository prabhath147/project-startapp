package com.order.service.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.order.service.client.auth.AuthClient;
import com.order.service.client.notify.EmailClient;
import com.order.service.client.pharmacy.ItemClient;
import com.order.service.controller.OrdersController;
import com.order.service.dto.AddressDto;
import com.order.service.dto.ItemDto;
import com.order.service.dto.OrdersDto;
import com.order.service.dto.PageableResponse;
import com.order.service.dto.ProductDto;
import com.order.service.dto.RepeatOrderDto;
import com.order.service.dtoexternal.EmailDto;
import com.order.service.dtoexternal.JwtResponse;
import com.order.service.dtoexternal.LoginRequest;
import com.order.service.exception.ResourceException;
import com.order.service.model.Address;
import com.order.service.model.RepeatOrder;
import com.order.service.repository.RepeatOrderRepository;
import com.order.service.service.OrdersService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = { RepeatOrderServiceImplementation.class })
@ExtendWith(SpringExtension.class)
class RepeatOrderServiceImplementationTest {
    @MockBean
    private AuthClient authClient;

    @MockBean
    private ItemClient itemClient;

    @MockBean
    private ModelMapper modelMapper;

    @MockBean
    private OrdersController ordersController;

    @MockBean
    private OrdersService ordersService;

    @MockBean
    private RepeatOrderRepository repeatOrderRepository;

    @Autowired
    private RepeatOrderServiceImplementation repeatOrderServiceImplementation;
    
    @MockBean
    private EmailClient emailClient;
    
    Address address;
    
    RepeatOrder repeatOrder;
    

    @BeforeEach
    public void setup() throws Exception{
    	Address address = new Address();
        address.setAddressId(123L);
        address.setCity("Oxford");
        address.setCountry("GB");
        address.setPinCode(RepeatOrderServiceImplementation.REPEAT_ORDER);
        address.setState("MD");
        address.setStreet(RepeatOrderServiceImplementation.REPEAT_ORDER);

        RepeatOrder repeatOrder = new RepeatOrder();
        repeatOrder.setAddress(address);
        repeatOrder.setDeliveryDate(LocalDate.ofEpochDay(1L));
        repeatOrder.setId(123L);
        repeatOrder.setIntervalInDays(42);
        repeatOrder.setName(RepeatOrderServiceImplementation.REPEAT_ORDER);
        repeatOrder.setNumberOfDeliveries(10);
        repeatOrder.setRepeatOrderItems(new HashSet<>());
        repeatOrder.setUserId(123L);
    }
    
    @Test
    void testCreateOptIn() {
        when(modelMapper.map((Object) any(), (Class<Object>) any())).thenReturn("Map");
        assertThrows(ResourceException.class, () -> repeatOrderServiceImplementation.createOptIn(new RepeatOrderDto()));
        verify(modelMapper).map((Object) any(), (Class<RepeatOrder>) any());
    }

    @Test
    void testCreateOptIn2() {
        Address address = new Address();
        address.setAddressId(123L);
        address.setCity("Oxford");
        address.setCountry("GB");
        address.setPinCode(RepeatOrderServiceImplementation.REPEAT_ORDER);
        address.setState("MD");
        address.setStreet(RepeatOrderServiceImplementation.REPEAT_ORDER);

        RepeatOrder repeatOrder = new RepeatOrder();
        repeatOrder.setAddress(address);
        repeatOrder.setDeliveryDate(LocalDate.ofEpochDay(1L));
        repeatOrder.setId(123L);
        repeatOrder.setIntervalInDays(42);
        repeatOrder.setName(RepeatOrderServiceImplementation.REPEAT_ORDER);
        repeatOrder.setNumberOfDeliveries(10);
        repeatOrder.setRepeatOrderItems(new HashSet<>());
        repeatOrder.setUserId(123L);
        when(modelMapper.map((Object) any(), (Class<Object>) any())).thenReturn(repeatOrder);

        Address address1 = new Address();
        address1.setAddressId(123L);
        address1.setCity("Oxford");
        address1.setCountry("GB");
        address1.setPinCode("Pin Code");
        address1.setState("MD");
        address1.setStreet("Street");

        RepeatOrder repeatOrder1 = new RepeatOrder();
        repeatOrder1.setAddress(address1);
        repeatOrder1.setDeliveryDate(LocalDate.ofEpochDay(1L));
        repeatOrder1.setId(123L);
        repeatOrder1.setIntervalInDays(42);
        repeatOrder1.setName("Name");
        repeatOrder1.setNumberOfDeliveries(10);
        repeatOrder1.setRepeatOrderItems(new HashSet<>());
        repeatOrder1.setUserId(123L);
        when(repeatOrderRepository.save((RepeatOrder) any())).thenReturn(repeatOrder1);
        assertThrows(ResourceException.class, () -> repeatOrderServiceImplementation.createOptIn(new RepeatOrderDto()));
        verify(modelMapper, atLeast(1)).map((Object) any(), (Class<Object>) any());
        verify(repeatOrderRepository).save((RepeatOrder) any());
    }

    @Test
    void testCreateOptIn3() {

        when(modelMapper.map((Object) any(), (Class<RepeatOrder>) any())).thenReturn(repeatOrder);
        when(repeatOrderRepository.save((RepeatOrder) any())).thenReturn(repeatOrder);
        RepeatOrderDto repeatOrderDto = new RepeatOrderDto();

        when(modelMapper.map((Object) any(), (Class<Object>) any())).thenReturn(null);
        assertNull(repeatOrderServiceImplementation.createOptIn(new RepeatOrderDto()));

        verify(modelMapper, atLeast(1)).map((Object) any(), (Class<Object>) any());

    }

    @Test
    void testCreateOptIn4() {
        Address address = new Address();
        address.setAddressId(123L);
        address.setCity("Oxford");
        address.setCountry("GB");
        address.setPinCode(RepeatOrderServiceImplementation.REPEAT_ORDER);
        address.setState("MD");
        address.setStreet(RepeatOrderServiceImplementation.REPEAT_ORDER);

        RepeatOrder repeatOrder = new RepeatOrder();
        repeatOrder.setAddress(address);
        repeatOrder.setDeliveryDate(LocalDate.ofEpochDay(1L));
        repeatOrder.setId(123L);
        repeatOrder.setIntervalInDays(42);
        repeatOrder.setName(RepeatOrderServiceImplementation.REPEAT_ORDER);
        repeatOrder.setNumberOfDeliveries(10);
        repeatOrder.setRepeatOrderItems(new HashSet<>());
        repeatOrder.setUserId(123L);
        when(modelMapper.map((Object) any(), (Class<Object>) any())).thenReturn(repeatOrder);
        when(repeatOrderRepository.save((RepeatOrder) any()))
                .thenThrow(new ResourceException(RepeatOrderServiceImplementation.REPEAT_ORDER,
                        RepeatOrderServiceImplementation.REPEAT_ORDER, "Field Value",
                        ResourceException.ErrorType.CREATED));
        assertThrows(ResourceException.class, () -> repeatOrderServiceImplementation.createOptIn(new RepeatOrderDto()));
        verify(modelMapper).map((Object) any(), (Class<RepeatOrder>) any());
        verify(repeatOrderRepository).save((RepeatOrder) any());
    }

    @Test
    void testUpdateOptIn() {
        when(modelMapper.map((Object) any(), (Class<Object>) any())).thenReturn("Map");

        Address address = new Address();
        address.setAddressId(123L);
        address.setCity("Oxford");
        address.setCountry("GB");
        address.setPinCode("Pin Code");
        address.setState("MD");
        address.setStreet("Street");

        RepeatOrder repeatOrder = new RepeatOrder();
        repeatOrder.setAddress(address);
        repeatOrder.setDeliveryDate(LocalDate.ofEpochDay(1L));
        repeatOrder.setId(123L);
        repeatOrder.setIntervalInDays(42);
        repeatOrder.setName("Name");
        repeatOrder.setNumberOfDeliveries(10);
        repeatOrder.setRepeatOrderItems(new HashSet<>());
        repeatOrder.setUserId(123L);
        Optional<RepeatOrder> ofResult = Optional.of(repeatOrder);
        when(repeatOrderRepository.findById((Long) any())).thenReturn(ofResult);
        assertThrows(ResourceException.class, () -> repeatOrderServiceImplementation.updateOptIn(new RepeatOrderDto()));
        verify(modelMapper).map((Object) any(), (Class<RepeatOrder>) any());
        verify(repeatOrderRepository).findById((Long) any());
    }

    @Test
    void testUpdateOptIn2() {
        when(modelMapper.map((Object) any(), (Class<Object>) any())).thenThrow(
                new ResourceException("Resource Name", "Field Name", "Field Value",
                        ResourceException.ErrorType.CREATED));

        Address address = new Address();
        address.setAddressId(123L);
        address.setCity("Oxford");
        address.setCountry("GB");
        address.setPinCode("Pin Code");
        address.setState("MD");
        address.setStreet("Street");

        RepeatOrder repeatOrder = new RepeatOrder();
        repeatOrder.setAddress(address);
        repeatOrder.setDeliveryDate(LocalDate.ofEpochDay(1L));
        repeatOrder.setId(123L);
        repeatOrder.setIntervalInDays(42);
        repeatOrder.setName("Name");
        repeatOrder.setNumberOfDeliveries(10);
        repeatOrder.setRepeatOrderItems(new HashSet<>());
        repeatOrder.setUserId(123L);
        Optional<RepeatOrder> ofResult = Optional.of(repeatOrder);
        when(repeatOrderRepository.findById((Long) any())).thenReturn(ofResult);
        assertThrows(ResourceException.class, () -> repeatOrderServiceImplementation.updateOptIn(new RepeatOrderDto()));
        verify(modelMapper).map((Object) any(), (Class<RepeatOrder>) any());
        verify(repeatOrderRepository).findById((Long) any());
    }

    @Test
    void testUpdateOptIn3() {
        when(modelMapper.map((Object) any(), (Class<Object>) any())).thenReturn("Map");

        Address address = new Address();
        address.setAddressId(123L);
        address.setCity("Oxford");
        address.setCountry("GB");
        address.setPinCode("Pin Code");
        address.setState("MD");
        address.setStreet("Street");

        RepeatOrder repeatOrder = new RepeatOrder();
        repeatOrder.setAddress(address);
        repeatOrder.setDeliveryDate(LocalDate.ofEpochDay(1L));
        repeatOrder.setId(123L);
        repeatOrder.setIntervalInDays(42);
        repeatOrder.setName("Name");
        repeatOrder.setNumberOfDeliveries(10);
        repeatOrder.setRepeatOrderItems(new HashSet<>());
        repeatOrder.setUserId(123L);
        when(repeatOrderRepository.save((RepeatOrder) any())).thenReturn(repeatOrder);
        when(repeatOrderRepository.findById((Long) any())).thenReturn(Optional.empty());
        assertThrows(ResourceException.class, () -> repeatOrderServiceImplementation.updateOptIn(new RepeatOrderDto()));
        verify(repeatOrderRepository).findById((Long) any());
    }

    @Test
    void testUpdateOptIn4() {
        Address address = new Address();
        address.setAddressId(123L);
        address.setCity("Oxford");
        address.setCountry("GB");
        address.setPinCode("Pin Code");
        address.setState("MD");
        address.setStreet("Street");

        RepeatOrder repeatOrder = new RepeatOrder();
        repeatOrder.setAddress(address);
        repeatOrder.setDeliveryDate(LocalDate.ofEpochDay(1L));
        repeatOrder.setId(123L);
        repeatOrder.setIntervalInDays(42);
        repeatOrder.setName("Name");
        repeatOrder.setNumberOfDeliveries(10);
        repeatOrder.setRepeatOrderItems(new HashSet<>());
        repeatOrder.setUserId(123L);
        when(modelMapper.map((Object) any(), (Class<RepeatOrder>) any())).thenReturn(repeatOrder);

        Address address1 = new Address();
        address1.setAddressId(123L);
        address1.setCity("Oxford");
        address1.setCountry("GB");
        address1.setPinCode("Pin Code");
        address1.setState("MD");
        address1.setStreet("Street");

        RepeatOrderDto repeatOrder1 = new RepeatOrderDto();
        repeatOrder1.setAddress(null);
        repeatOrder1.setDeliveryDate(LocalDate.ofEpochDay(1L));
        repeatOrder1.setId(123L);
        repeatOrder1.setIntervalInDays(42);
        repeatOrder1.setName("Name");
        repeatOrder1.setNumberOfDeliveries(10);
        repeatOrder1.setRepeatOrderItems(new HashSet<>());
        repeatOrder1.setUserId(123L);
        Optional<RepeatOrder> ofResult = Optional.of(repeatOrder);
        when(repeatOrderRepository.save((RepeatOrder) any())).thenReturn(repeatOrder);
        RepeatOrderDto repeatOrderDto = new RepeatOrderDto();

        when(repeatOrderRepository.findById(123L)).thenReturn(ofResult);
        when(modelMapper.map((Object) any(), (Class<Object>) any())).thenReturn(null);
        assertNull(repeatOrderServiceImplementation.updateOptIn(repeatOrder1));

        verify(modelMapper, atLeast(1)).map((Object) any(), (Class<Object>) any());
    }

    @Test
    void testGetOptInIdAndPlaceOrder() {
        when(modelMapper.map((Object) any(), (Class<RepeatOrderDto>) any())).thenReturn(new RepeatOrderDto());

        Address address = new Address();
        address.setAddressId(123L);
        address.setCity("Oxford");
        address.setCountry("GB");
        address.setPinCode("Pin Code");
        address.setState("MD");
        address.setStreet("Street");

        RepeatOrder repeatOrder = new RepeatOrder();
        repeatOrder.setAddress(address);
        repeatOrder.setDeliveryDate(LocalDate.ofEpochDay(1L));
        repeatOrder.setId(123L);
        repeatOrder.setIntervalInDays(42);
        repeatOrder.setName("Name");
        repeatOrder.setNumberOfDeliveries(10);
        repeatOrder.setRepeatOrderItems(new HashSet<>());
        repeatOrder.setUserId(123L);
        Optional<RepeatOrder> ofResult = Optional.of(repeatOrder);
        when(repeatOrderRepository.findById((Long) any())).thenReturn(ofResult);
        assertThrows(ResourceException.class, () -> repeatOrderServiceImplementation.getOptInIdAndPlaceOrder(123L));
        verify(modelMapper).map((Object) any(), (Class<RepeatOrderDto>) any());
        verify(repeatOrderRepository).findById((Long) any());
    }

    @Test
    void testGetOptInIdAndPlaceOrder2() {
        when(modelMapper.map((Object) any(), (Class<RepeatOrderDto>) any())).thenThrow(
                new ResourceException("Resource Name", "Field Name", "Field Value",
                        ResourceException.ErrorType.CREATED));

        Address address = new Address();
        address.setAddressId(123L);
        address.setCity("Oxford");
        address.setCountry("GB");
        address.setPinCode("Pin Code");
        address.setState("MD");
        address.setStreet("Street");

        RepeatOrder repeatOrder = new RepeatOrder();
        repeatOrder.setAddress(address);
        repeatOrder.setDeliveryDate(LocalDate.ofEpochDay(1L));
        repeatOrder.setId(123L);
        repeatOrder.setIntervalInDays(42);
        repeatOrder.setName("Name");
        repeatOrder.setNumberOfDeliveries(10);
        repeatOrder.setRepeatOrderItems(new HashSet<>());
        repeatOrder.setUserId(123L);
        Optional<RepeatOrder> ofResult = Optional.of(repeatOrder);
        when(repeatOrderRepository.findById((Long) any())).thenReturn(ofResult);
        assertThrows(ResourceException.class, () -> repeatOrderServiceImplementation.getOptInIdAndPlaceOrder(123L));
        verify(modelMapper).map((Object) any(), (Class<RepeatOrderDto>) any());
        verify(repeatOrderRepository).findById((Long) any());
    }

    @Test
    void testGetOptInIdAndPlaceOrder3() {
        LocalDate deliveryDate = LocalDate.ofEpochDay(1L);
        AddressDto address = new AddressDto();
        when(modelMapper.map((Object) any(), (Class<RepeatOrderDto>) any()))
                .thenReturn(new RepeatOrderDto(123L, 123L, "Optin", 10, 42, deliveryDate, address, new HashSet<>()));
        when(ordersController.placeOrder((OrdersDto) any())).thenReturn(new ResponseEntity<>(HttpStatus.CONTINUE));

        Address address1 = new Address();
        address1.setAddressId(123L);
        address1.setCity("Oxford");
        address1.setCountry("GB");
        address1.setPinCode("Pin Code");
        address1.setState("MD");
        address1.setStreet("Street");

        RepeatOrder repeatOrder = new RepeatOrder();
        repeatOrder.setAddress(address1);
        repeatOrder.setDeliveryDate(LocalDate.ofEpochDay(1L));
        repeatOrder.setId(123L);
        repeatOrder.setIntervalInDays(42);
        repeatOrder.setName("Name");
        repeatOrder.setNumberOfDeliveries(10);
        repeatOrder.setRepeatOrderItems(new HashSet<>());
        repeatOrder.setUserId(123L);
        Optional<RepeatOrder> ofResult = Optional.of(repeatOrder);
        when(repeatOrderRepository.findById((Long) any())).thenReturn(ofResult);
        assertNull(repeatOrderServiceImplementation.getOptInIdAndPlaceOrder(123L));
        verify(modelMapper).map((Object) any(), (Class<RepeatOrderDto>) any());
        verify(ordersController).placeOrder((OrdersDto) any());
        verify(repeatOrderRepository).findById((Long) any());
    }

    @Test
    void testGetOptInIdAndPlaceOrder7() {
        LocalDate deliveryDate = LocalDate.ofEpochDay(1L);
        AddressDto address = new AddressDto();
        when(modelMapper.map((Object) any(), (Class<RepeatOrderDto>) any()))
                .thenReturn(new RepeatOrderDto(123L, 123L, "Optin", 10, 42, deliveryDate, address, new HashSet<>()));
        when(ordersController.placeOrder((OrdersDto) any())).thenReturn(null);

        Address address1 = new Address();
        address1.setAddressId(123L);
        address1.setCity("Oxford");
        address1.setCountry("GB");
        address1.setPinCode("Pin Code");
        address1.setState("MD");
        address1.setStreet("Street");

        RepeatOrder repeatOrder = new RepeatOrder();
        repeatOrder.setAddress(address1);
        repeatOrder.setDeliveryDate(LocalDate.ofEpochDay(1L));
        repeatOrder.setId(123L);
        repeatOrder.setIntervalInDays(42);
        repeatOrder.setName("Name");
        repeatOrder.setNumberOfDeliveries(10);
        repeatOrder.setRepeatOrderItems(new HashSet<>());
        repeatOrder.setUserId(123L);
        Optional<RepeatOrder> ofResult = Optional.of(repeatOrder);
        when(repeatOrderRepository.findById((Long) any())).thenReturn(ofResult);
        assertThrows(ResourceException.class, () -> repeatOrderServiceImplementation.getOptInIdAndPlaceOrder(123L));
        verify(modelMapper).map((Object) any(), (Class<RepeatOrderDto>) any());
        verify(ordersController).placeOrder((OrdersDto) any());
        verify(repeatOrderRepository).findById((Long) any());
    }

    @Test
    void testGetOptInIdAndPlaceOrder8() {
        LocalDate deliveryDate = LocalDate.ofEpochDay(1L);
        AddressDto address = new AddressDto();
        when(modelMapper.map((Object) any(), (Class<RepeatOrderDto>) any()))
                .thenReturn(new RepeatOrderDto(123L, 123L, "Optin", 10, 42, deliveryDate, address, new HashSet<>()));
        ResponseEntity<OrdersDto> responseEntity = (ResponseEntity<OrdersDto>) mock(ResponseEntity.class);
        when(responseEntity.getStatusCodeValue()).thenThrow(
                new ResourceException("Resource Name", "Field Name", "Field Value",
                        ResourceException.ErrorType.CREATED));
        when(responseEntity.getBody()).thenThrow(
                new ResourceException("Resource Name", "Field Name", "Field Value",
                        ResourceException.ErrorType.CREATED));
        when(ordersController.placeOrder((OrdersDto) any())).thenReturn(responseEntity);

        Address address1 = new Address();
        address1.setAddressId(123L);
        address1.setCity("Oxford");
        address1.setCountry("GB");
        address1.setPinCode("Pin Code");
        address1.setState("MD");
        address1.setStreet("Street");

        RepeatOrder repeatOrder = new RepeatOrder();
        repeatOrder.setAddress(address1);
        repeatOrder.setDeliveryDate(LocalDate.ofEpochDay(1L));
        repeatOrder.setId(123L);
        repeatOrder.setIntervalInDays(42);
        repeatOrder.setName("Name");
        repeatOrder.setNumberOfDeliveries(10);
        repeatOrder.setRepeatOrderItems(new HashSet<>());
        repeatOrder.setUserId(123L);
        Optional<RepeatOrder> ofResult = Optional.of(repeatOrder);
        when(repeatOrderRepository.findById((Long) any())).thenReturn(ofResult);
        assertThrows(ResourceException.class, () -> repeatOrderServiceImplementation.getOptInIdAndPlaceOrder(123L));
        verify(modelMapper).map((Object) any(), (Class<RepeatOrderDto>) any());
        verify(ordersController).placeOrder((OrdersDto) any());
        verify(responseEntity).getStatusCodeValue();
        verify(repeatOrderRepository).findById((Long) any());
    }

    @Test
    void testSetOrderAddress() {
        AddressDto actualSetOrderAddressResult = repeatOrderServiceImplementation.setOrderAddress(new AddressDto());
        assertNull(actualSetOrderAddressResult.getStreet());
        assertNull(actualSetOrderAddressResult.getState());
        assertNull(actualSetOrderAddressResult.getPinCode());
        assertNull(actualSetOrderAddressResult.getCountry());
        assertNull(actualSetOrderAddressResult.getCity());
    }

    @Test
    void testSetOrderAddress3() {
        AddressDto addressDto = mock(AddressDto.class);
        when(addressDto.getCity()).thenReturn("Oxford");
        when(addressDto.getCountry()).thenReturn("GB");
        when(addressDto.getPinCode()).thenReturn("Pin Code");
        when(addressDto.getState()).thenReturn("MD");
        when(addressDto.getStreet()).thenReturn("Street");
        AddressDto actualSetOrderAddressResult = repeatOrderServiceImplementation.setOrderAddress(addressDto);
        assertEquals("Street", actualSetOrderAddressResult.getStreet());
        assertEquals("MD", actualSetOrderAddressResult.getState());
        assertEquals("Pin Code", actualSetOrderAddressResult.getPinCode());
        assertEquals("GB", actualSetOrderAddressResult.getCountry());
        assertEquals("Oxford", actualSetOrderAddressResult.getCity());
        verify(addressDto).getCity();
        verify(addressDto).getCountry();
        verify(addressDto).getPinCode();
        verify(addressDto).getState();
        verify(addressDto).getStreet();
    }

    @Test
    void testGetOptInToSendNotification3() {
        when(authClient.authenticateUser((LoginRequest) any()))
                .thenReturn(new ResponseEntity<>(new JwtResponse(), HttpStatus.CONTINUE));
        when(repeatOrderRepository.findAllByDeliveryDate((LocalDate) any())).thenReturn(new ArrayList<>());
        // assertTrue(repeatOrderServiceImplementation.getOptInToSendNotification(LocalDate.ofEpochDay(1L)).isEmpty());
        // verify(repeatOrderServiceImplementation).getOptInToSendNotification((LocalDate)
        // any());
        repeatOrderServiceImplementation.getOptInToSendNotification(LocalDate.ofEpochDay(1L));
        verify(authClient).authenticateUser((LoginRequest) any());
        verify(repeatOrderRepository).findAllByDeliveryDate((LocalDate) any());
    }

    @Test
    void testGetOptInToSendNotification4() {
        when(authClient.authenticateUser((LoginRequest) any()))
                .thenReturn(new ResponseEntity<>(new JwtResponse(), HttpStatus.CONTINUE));
        when(modelMapper.map((Object) any(), (Class<RepeatOrderDto>) any())).thenReturn(new RepeatOrderDto());

        Address address = new Address();
        address.setAddressId(123L);
        address.setCity("Oxford");
        address.setCountry("GB");
        address.setPinCode("admin1");
        address.setState("MD");
        address.setStreet("admin1");

        RepeatOrder repeatOrder = new RepeatOrder();
        repeatOrder.setAddress(address);
        repeatOrder.setDeliveryDate(LocalDate.ofEpochDay(3L));
        repeatOrder.setId(123L);
        repeatOrder.setIntervalInDays(42);
        repeatOrder.setName("admin1");
        repeatOrder.setNumberOfDeliveries(10);
        repeatOrder.setRepeatOrderItems(new HashSet<>());
        repeatOrder.setUserId(123L);

        ArrayList<RepeatOrder> repeatOrderList = new ArrayList<>();
        repeatOrderList.add(repeatOrder);
        when(repeatOrderRepository.findAllByDeliveryDate((LocalDate) any())).thenReturn(repeatOrderList);
        // assertTrue(repeatOrderServiceImplementation.getOptInToSendNotification(LocalDate.ofEpochDay(1L)).isEmpty());
        repeatOrderServiceImplementation.getOptInToSendNotification(LocalDate.ofEpochDay(1L));
        verify(authClient).authenticateUser((LoginRequest) any());
        verify(modelMapper).map((Object) any(), (Class<RepeatOrderDto>) any());
        verify(repeatOrderRepository).findAllByDeliveryDate((LocalDate) any());
    }

    @Test
    void testGetOptInToSendNotification7() {
        JwtResponse res = new JwtResponse();
        res.setToken("abc");
        res.setType("ab");
        when(authClient.authenticateUser((LoginRequest) any()))
                .thenReturn(new ResponseEntity<>(res, HttpStatus.CONTINUE));

        Address address = new Address();
        address.setAddressId(123L);
        address.setCity("Oxford");
        address.setCountry("GB");
        address.setPinCode("Pin Code");
        address.setState("MD");
        address.setStreet("Street");

        ProductDto product = new ProductDto();
        product.setPrice(12D);
        product.setProductId(1L);
        product.setProductIdFk(2L);
        product.setQuantity(21L);

        Set<ProductDto> repeatSetDto = new HashSet<>();
        repeatSetDto.add(product);
        RepeatOrder repeatOrder = new RepeatOrder();
        repeatOrder.setAddress(address);
        repeatOrder.setDeliveryDate(LocalDate.ofEpochDay(3L));
        repeatOrder.setId(123L);
        repeatOrder.setIntervalInDays(42);
        repeatOrder.setName("Name");
        repeatOrder.setNumberOfDeliveries(10);
        repeatOrder.setRepeatOrderItems(new HashSet<>());
        repeatOrder.setUserId(123L);

        RepeatOrderDto repeatOrder1 = new RepeatOrderDto();
        repeatOrder1.setAddress(null);
        repeatOrder1.setDeliveryDate(LocalDate.ofEpochDay(1L));
        repeatOrder1.setId(123L);
        repeatOrder1.setIntervalInDays(42);
        repeatOrder1.setName("Name");
        repeatOrder1.setNumberOfDeliveries(10);
        repeatOrder1.setRepeatOrderItems(repeatSetDto);
        repeatOrder1.setUserId(123L);
        when(modelMapper.map((Object) any(), (Class<RepeatOrderDto>) any())).thenReturn(repeatOrder1);
        ArrayList<RepeatOrder> repeatOrderList = new ArrayList<>();
        repeatOrderList.add(repeatOrder);
        repeatOrderList.add(repeatOrder);

        ItemDto item = new ItemDto();
        item.setItemId(123L);
        item.setPrice(23D);
        item.setItemQuantity(20L);
        List<ItemDto> itemList = new ArrayList<>();
        itemList.add(item);

        when(itemClient.getItemsByProductIdWithHeader((String) any(), (Integer) any(), (Integer) any(), (Long) any()))
                .thenReturn(
                        new ResponseEntity<>(new PageableResponse<>(new ArrayList<>(itemList), 10, 3, 1L, 1, true),
                                HttpStatus.CONTINUE));
        when(itemClient.getItemsByProductId((Integer) any(), (Integer) any(), (Long) any())).thenReturn(
                new ResponseEntity<>(new PageableResponse<>(new ArrayList<>(itemList), 10, 3, 1L, 1, true),
                        HttpStatus.CONTINUE));
        // when(repeatOrderServiceImplementation.getItemByProductId(1L)).thenReturn(item);
        when(repeatOrderRepository.findAllByDeliveryDate((LocalDate) any())).thenReturn(repeatOrderList);

        // assertFalse(repeatOrderServiceImplementation.getOptInToSendNotification(LocalDate.ofEpochDay(1L)).isEmpty());
        repeatOrderServiceImplementation.getOptInToSendNotification(LocalDate.ofEpochDay(1L));

    }

    @Test
    void testGetAllOptInByUserId() {
        ArrayList<RepeatOrder> repeatOrderList = new ArrayList<>();
        when(repeatOrderRepository.findAllByUserId((Long) any(), (Pageable) any()))
                .thenReturn(new PageImpl<>(repeatOrderList));
        PageableResponse<RepeatOrderDto> actualAllOptInByUserId = repeatOrderServiceImplementation
                .getAllOptInByUserId(123L, 10, 3);
        assertEquals(repeatOrderList, actualAllOptInByUserId.getData());
        assertEquals(0L, actualAllOptInByUserId.getTotalRecords().longValue());
        assertEquals(1, actualAllOptInByUserId.getTotalPages().intValue());
        assertEquals(0, actualAllOptInByUserId.getPageSize().intValue());
        assertEquals(0, actualAllOptInByUserId.getPageNumber().intValue());
        assertTrue(actualAllOptInByUserId.getIsLastPage());
        verify(repeatOrderRepository).findAllByUserId((Long) any(), (Pageable) any());
    }

    @Test
    void testGetAllOptInByUserId2() {
        when(modelMapper.map((Object) any(), (Class<RepeatOrderDto>) any())).thenReturn(new RepeatOrderDto());

        Address address = new Address();
        address.setAddressId(123L);
        address.setCity("Oxford");
        address.setCountry("GB");
        address.setPinCode("Pin Code");
        address.setState("MD");
        address.setStreet("Street");

        RepeatOrder repeatOrder = new RepeatOrder();
        repeatOrder.setAddress(address);
        repeatOrder.setDeliveryDate(LocalDate.ofEpochDay(1L));
        repeatOrder.setId(123L);
        repeatOrder.setIntervalInDays(42);
        repeatOrder.setName("Name");
        repeatOrder.setNumberOfDeliveries(10);
        repeatOrder.setRepeatOrderItems(new HashSet<>());
        repeatOrder.setUserId(123L);

        ArrayList<RepeatOrder> repeatOrderList = new ArrayList<>();
        repeatOrderList.add(repeatOrder);
        PageImpl<RepeatOrder> pageImpl = new PageImpl<>(repeatOrderList);
        when(repeatOrderRepository.findAllByUserId((Long) any(), (Pageable) any())).thenReturn(pageImpl);
        PageableResponse<RepeatOrderDto> actualAllOptInByUserId = repeatOrderServiceImplementation
                .getAllOptInByUserId(123L, 10, 3);
        assertEquals(1, actualAllOptInByUserId.getData().size());
        assertEquals(1L, actualAllOptInByUserId.getTotalRecords().longValue());
        assertEquals(1, actualAllOptInByUserId.getTotalPages().intValue());
        assertEquals(1, actualAllOptInByUserId.getPageSize().intValue());
        assertEquals(0, actualAllOptInByUserId.getPageNumber().intValue());
        assertTrue(actualAllOptInByUserId.getIsLastPage());
        verify(modelMapper).map((Object) any(), (Class<RepeatOrderDto>) any());
        verify(repeatOrderRepository).findAllByUserId((Long) any(), (Pageable) any());
    }

    @Test
    void testGetOptInById() {
        RepeatOrderDto repeatOrderDto = new RepeatOrderDto();
        when(modelMapper.map((Object) any(), (Class<RepeatOrderDto>) any())).thenReturn(repeatOrderDto);

        Address address = new Address();
        address.setAddressId(123L);
        address.setCity("Oxford");
        address.setCountry("GB");
        address.setPinCode("Pin Code");
        address.setState("MD");
        address.setStreet("Street");

        RepeatOrder repeatOrder = new RepeatOrder();
        repeatOrder.setAddress(address);
        repeatOrder.setDeliveryDate(LocalDate.ofEpochDay(1L));
        repeatOrder.setId(123L);
        repeatOrder.setIntervalInDays(42);
        repeatOrder.setName("Name");
        repeatOrder.setNumberOfDeliveries(10);
        repeatOrder.setRepeatOrderItems(new HashSet<>());
        repeatOrder.setUserId(123L);
        Optional<RepeatOrder> ofResult = Optional.of(repeatOrder);
        when(repeatOrderRepository.findById((Long) any())).thenReturn(ofResult);
        assertSame(repeatOrderDto, repeatOrderServiceImplementation.getOptInById(123L));
        verify(modelMapper).map((Object) any(), (Class<RepeatOrderDto>) any());
        verify(repeatOrderRepository).findById((Long) any());
    }

    @Test
    void testGetOptInById2() {

        Optional<RepeatOrder> ofResult = Optional.empty();
        when(repeatOrderRepository.findById((Long) any())).thenReturn(ofResult);
        assertThrows(ResourceException.class, () -> repeatOrderServiceImplementation.getOptInById(123L));

    }

    @Test
    void testGetOptInIdAndPlaceOrder9() {
        LocalDate deliveryDate = LocalDate.ofEpochDay(1L);
        AddressDto address1 = new AddressDto();
        address1.setAddressId(123L);
        address1.setCity("Oxford");
        address1.setCountry("GB");
        address1.setPinCode("Pin Code");
        address1.setState("MD");
        address1.setStreet("Street");
        when(modelMapper.map((Object) any(), (Class<RepeatOrderDto>) any()))
                .thenReturn(new RepeatOrderDto(123L, 123L, "Optin", 10, 42, deliveryDate, address1, new HashSet<>()));
        when(ordersController.placeOrder((OrdersDto) any())).thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

        Address address2 = new Address();
        address2.setAddressId(123L);
        address2.setCity("Oxford");
        address2.setCountry("GB");
        address2.setPinCode("Pin Code");
        address2.setState("MD");
        address2.setStreet("Street");

        RepeatOrder repeatOrder = new RepeatOrder();
        repeatOrder.setAddress(address2);
        repeatOrder.setDeliveryDate(LocalDate.ofEpochDay(1L));
        repeatOrder.setId(123L);
        repeatOrder.setIntervalInDays(42);
        repeatOrder.setName("Name");
        repeatOrder.setNumberOfDeliveries(10);
        repeatOrder.setRepeatOrderItems(new HashSet<>());
        repeatOrder.setUserId(123L);
        Optional<RepeatOrder> ofResult = Optional.of(repeatOrder);
        when(repeatOrderRepository.findById((Long) any())).thenReturn(ofResult);

        ProductDto product = new ProductDto();
        product.setPrice(12D);
        product.setProductId(1L);
        product.setProductIdFk(2L);
        product.setQuantity(2L);
        // RepeatOrderItemDto repeatItem=new RepeatOrderItemDto();

        Set<ProductDto> repeatSetDto = new HashSet<>();
        repeatSetDto.add(product);

        RepeatOrderDto repeatOrder1 = new RepeatOrderDto();
        repeatOrder1.setAddress(address1);
        repeatOrder1.setDeliveryDate(LocalDate.ofEpochDay(1L));
        repeatOrder1.setId(123L);
        repeatOrder1.setIntervalInDays(42);
        repeatOrder1.setName("Name");
        repeatOrder1.setNumberOfDeliveries(10);
        repeatOrder1.setRepeatOrderItems(repeatSetDto);
        repeatOrder1.setUserId(123L);
        when(modelMapper.map((Object) any(), (Class<RepeatOrderDto>) any())).thenReturn(repeatOrder1);

        ItemDto item = new ItemDto();
        item.setItemId(123L);
        item.setPrice(23D);
        item.setItemQuantity(3L);
        List<ItemDto> itemList = new ArrayList<>();
        itemList.add(item);
        when(itemClient.getItemsByProductId((Integer) any(), (Integer) any(), (Long) any())).thenReturn(
                new ResponseEntity<>(new PageableResponse<>(new ArrayList<>(itemList), 10, 3, 1L, 1, true),
                        HttpStatus.CONTINUE));

        // assertNull(repeatOrderServiceImplementation.getOptInIdAndPlaceOrder(1L));
        assertThrows(ResourceException.class, () -> repeatOrderServiceImplementation.getOptInIdAndPlaceOrder(1L));

    }

    @Test
    void testGetOptInIdAndPlaceOrder10() {
        LocalDate deliveryDate = LocalDate.ofEpochDay(1L);
        AddressDto address1 = new AddressDto();
        address1.setAddressId(123L);
        address1.setCity("Oxford");
        address1.setCountry("GB");
        address1.setPinCode("Pin Code");
        address1.setState("MD");
        address1.setStreet("Street");
        when(modelMapper.map((Object) any(), (Class<RepeatOrderDto>) any()))
                .thenReturn(new RepeatOrderDto(123L, 123L, "Optin", 10, 42, deliveryDate, address1, new HashSet<>()));
        when(ordersController.placeOrder((OrdersDto) any())).thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

        Address address2 = new Address();
        address2.setAddressId(123L);
        address2.setCity("Oxford");
        address2.setCountry("GB");
        address2.setPinCode("Pin Code");
        address2.setState("MD");
        address2.setStreet("Street");

        RepeatOrder repeatOrder = new RepeatOrder();
        repeatOrder.setAddress(address2);
        repeatOrder.setDeliveryDate(LocalDate.ofEpochDay(1L));
        repeatOrder.setId(123L);
        repeatOrder.setIntervalInDays(42);
        repeatOrder.setName("Name");
        repeatOrder.setNumberOfDeliveries(10);
        repeatOrder.setRepeatOrderItems(new HashSet<>());
        repeatOrder.setUserId(123L);
        Optional<RepeatOrder> ofResult = Optional.empty();
        when(repeatOrderRepository.findById((Long) any())).thenReturn(ofResult);

        ProductDto product = new ProductDto();
        product.setPrice(12D);
        product.setProductId(1L);
        product.setProductIdFk(2L);
        product.setQuantity(2L);
        // RepeatOrderItemDto repeatItem=new RepeatOrderItemDto();

        Set<ProductDto> repeatSetDto = new HashSet<>();
        repeatSetDto.add(product);

        RepeatOrderDto repeatOrder1 = new RepeatOrderDto();
        repeatOrder1.setAddress(address1);
        repeatOrder1.setDeliveryDate(LocalDate.ofEpochDay(1L));
        repeatOrder1.setId(123L);
        repeatOrder1.setIntervalInDays(42);
        repeatOrder1.setName("Name");
        repeatOrder1.setNumberOfDeliveries(10);
        repeatOrder1.setRepeatOrderItems(repeatSetDto);
        repeatOrder1.setUserId(123L);
        when(modelMapper.map((Object) any(), (Class<RepeatOrderDto>) any())).thenReturn(repeatOrder1);

        ItemDto item = new ItemDto();
        item.setItemId(123L);
        item.setPrice(23D);
        item.setItemQuantity(3L);
        List<ItemDto> itemList = new ArrayList<>();
        itemList.add(item);
        when(itemClient.getItemsByProductId((Integer) any(), (Integer) any(), (Long) any())).thenReturn(
                new ResponseEntity<>(new PageableResponse<>(new ArrayList<>(itemList), 10, 3, 1L, 1, true),
                        HttpStatus.CONTINUE));

        // assertNull(repeatOrderServiceImplementation.getOptInIdAndPlaceOrder(1L));
        assertThrows(ResourceException.class, () -> repeatOrderServiceImplementation.getOptInIdAndPlaceOrder(1L));

    }

    @Test
    void testDeleteOptIn() {
        Address address = new Address();
        address.setAddressId(123L);
        address.setCity("Oxford");
        address.setCountry("GB");
        address.setPinCode("Pin Code");
        address.setState("MD");
        address.setStreet("Street");

        RepeatOrder repeatOrder = new RepeatOrder();
        repeatOrder.setAddress(address);
        repeatOrder.setDeliveryDate(LocalDate.ofEpochDay(1L));
        repeatOrder.setId(123L);
        repeatOrder.setIntervalInDays(42);
        repeatOrder.setName("Name");
        repeatOrder.setNumberOfDeliveries(10);
        repeatOrder.setRepeatOrderItems(new HashSet<>());
        repeatOrder.setUserId(123L);
        Optional<RepeatOrder> ofResult = Optional.of(repeatOrder);
        doNothing().when(repeatOrderRepository).deleteById((Long) any());
        when(repeatOrderRepository.findById((Long) any())).thenReturn(ofResult);
        repeatOrderServiceImplementation.deleteOptIn(123L);
        verify(repeatOrderRepository).findById((Long) any());
        verify(repeatOrderRepository).deleteById((Long) any());
    }

    @Test
    void testDeleteOptIn2() {
        Address address = new Address();
        address.setAddressId(123L);
        address.setCity("Oxford");
        address.setCountry("GB");
        address.setPinCode("Pin Code");
        address.setState("MD");
        address.setStreet("Street");

        RepeatOrder repeatOrder = new RepeatOrder();
        repeatOrder.setAddress(address);
        repeatOrder.setDeliveryDate(LocalDate.ofEpochDay(1L));
        repeatOrder.setId(123L);
        repeatOrder.setIntervalInDays(42);
        repeatOrder.setName("Name");
        repeatOrder.setNumberOfDeliveries(10);
        repeatOrder.setRepeatOrderItems(new HashSet<>());
        repeatOrder.setUserId(123L);
        Optional<RepeatOrder> ofResult = Optional.of(repeatOrder);
        doThrow(new ResourceException("Resource Name", "Field Name", "Field Value",
                ResourceException.ErrorType.CREATED))
                .when(repeatOrderRepository)
                .deleteById((Long) any());
        when(repeatOrderRepository.findById((Long) any())).thenReturn(ofResult);
        assertThrows(ResourceException.class, () -> repeatOrderServiceImplementation.deleteOptIn(123L));
        verify(repeatOrderRepository).findById((Long) any());
        verify(repeatOrderRepository).deleteById((Long) any());
    }

    @Test
    void testGetOptInToSendNotification5() {
        doNothing().when(authClient).logout((String) any());
        when(authClient.authenticateUser((LoginRequest) any()))
                .thenReturn(new ResponseEntity<>(new JwtResponse(), HttpStatus.CONTINUE));
        when(emailClient.sendBulkEmail((String) any(), (List<EmailDto>) any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.CONTINUE));
        when(repeatOrderRepository.findAllByDeliveryDate((LocalDate) any())).thenReturn(new ArrayList<>());
        // assertTrue(repeatOrderServiceImplementation.getOptInToSendNotification(LocalDate.ofEpochDay(1L)).isEmpty());
        repeatOrderServiceImplementation.getOptInToSendNotification(LocalDate.ofEpochDay(1L));
        verify(authClient).authenticateUser((LoginRequest) any());
        verify(authClient).logout((String) any());
        verify(emailClient).sendBulkEmail((String) any(), (List<EmailDto>) any());
        verify(repeatOrderRepository).findAllByDeliveryDate((LocalDate) any());
    }

    @Test
    void testGetOptInToSendNotification6() {
        doNothing().when(authClient).logout((String) any());
        when(authClient.authenticateUser((LoginRequest) any()))
                .thenReturn(new ResponseEntity<>(new JwtResponse(), HttpStatus.CONTINUE));
        when(emailClient.sendBulkEmail((String) any(), (List<EmailDto>) any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.CONTINUE));
        when(repeatOrderRepository.findAllByDeliveryDate((LocalDate) any()))
                .thenThrow(
                        new ResourceException("admin1", "admin1", "Field Value", ResourceException.ErrorType.CREATED));
        assertThrows(ResourceException.class,
                () -> repeatOrderServiceImplementation.getOptInToSendNotification(LocalDate.ofEpochDay(1L)));
        verify(authClient).authenticateUser((LoginRequest) any());
        verify(repeatOrderRepository).findAllByDeliveryDate((LocalDate) any());
    }

    @Test
    void testGetOptInToSendNotification8() {
        doNothing().when(authClient).logout((String) any());
        when(authClient.authenticateUser((LoginRequest) any()))
                .thenReturn(new ResponseEntity<>(new JwtResponse(), HttpStatus.CONTINUE));
        when(emailClient.sendBulkEmail((String) any(), (List<EmailDto>) any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.CONTINUE));
        when(modelMapper.map((Object) any(), (Class<RepeatOrderDto>) any())).thenReturn(new RepeatOrderDto());

        ArrayList<RepeatOrder> repeatOrderList = new ArrayList<>();
        repeatOrderList.add(repeatOrder);
        when(repeatOrderRepository.findAllByDeliveryDate((LocalDate) any())).thenReturn(repeatOrderList);
        // assertTrue(repeatOrderServiceImplementation.getOptInToSendNotification(LocalDate.ofEpochDay(1L)).isEmpty());
        repeatOrderServiceImplementation.getOptInToSendNotification(LocalDate.ofEpochDay(1L));
        verify(authClient).authenticateUser((LoginRequest) any());
        verify(authClient).logout((String) any());
        verify(emailClient).sendBulkEmail((String) any(), (List<EmailDto>) any());
        verify(modelMapper).map((Object) any(), (Class<RepeatOrderDto>) any());
        verify(repeatOrderRepository).findAllByDeliveryDate((LocalDate) any());
    }

    @Test
    void testDeleteOptIn3() {
        doNothing().when(repeatOrderRepository).deleteById((Long) any());
        when(repeatOrderRepository.findById((Long) any())).thenReturn(Optional.empty());
        assertThrows(ResourceException.class, () -> repeatOrderServiceImplementation.deleteOptIn(123L));
        verify(repeatOrderRepository).findById((Long) any());
    }
}
