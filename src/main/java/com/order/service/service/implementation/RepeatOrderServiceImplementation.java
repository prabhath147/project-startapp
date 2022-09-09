package com.order.service.service.implementation;

import com.order.service.client.auth.AuthClient;
import com.order.service.client.notify.EmailClient;
import com.order.service.client.pharmacy.ItemClient;
import com.order.service.client.pharmacy.StoreClient;
import com.order.service.controller.OrdersController;
import com.order.service.dto.AddressDto;
import com.order.service.dto.CartDto;
import com.order.service.dto.ItemDto;

import com.order.service.dto.OrdersDto;
import com.order.service.dto.PageableResponse;
import com.order.service.dto.ProductDto;
import com.order.service.dto.RepeatOrderDto;
import com.order.service.dto.RepeatOrderItemDto;
import com.order.service.dto.UserOptInDto;
import com.order.service.dtoexternal.EmailDto;
import com.order.service.dtoexternal.JwtResponse;
import com.order.service.dtoexternal.LoginRequest;
import com.order.service.exception.ResourceException;
import com.order.service.model.RepeatOrder;

import com.order.service.repository.RepeatOrderRepository;
import com.order.service.service.OrdersService;
import com.order.service.service.RepeatOrderService;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class RepeatOrderServiceImplementation implements RepeatOrderService {

	@Autowired
	private RepeatOrderRepository repeatOrderRepository;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	OrdersService ordersService;

	@Autowired
	ItemClient itemClient;

	@Autowired
	AuthClient authClient;

	@Autowired
	private EmailClient emailClient;
	 
	@Autowired
	private StoreClient storeClient;

	public static final String REPEAT_ORDER = "ReapeatOrder";

	@Override
	public RepeatOrderDto createOptIn(RepeatOrderDto repeatOrderDto) {
		try {
			RepeatOrder repeatOrder = modelMapper.map(repeatOrderDto, RepeatOrder.class);
			return modelMapper.map(repeatOrderRepository.save(repeatOrder), RepeatOrderDto.class);
		} catch (Exception e) {
			throw new ResourceException(REPEAT_ORDER, "repeatOrderDto", repeatOrderDto,
					ResourceException.ErrorType.CREATED, e);
		}
	}

	@Override
	public RepeatOrderDto updateOptIn(RepeatOrderDto repeatOrderDto) {

		Optional<RepeatOrder> repeatOrder = repeatOrderRepository.findById(repeatOrderDto.getId());
		if (repeatOrder.isEmpty()) {
			throw new ResourceException(REPEAT_ORDER, "repeatOrderDto", repeatOrderDto,
					ResourceException.ErrorType.FOUND);
		}
		try {

			return modelMapper.map(repeatOrderRepository.save(modelMapper.map(repeatOrderDto, RepeatOrder.class)),
					RepeatOrderDto.class);
		} catch (Exception e) {
			throw new ResourceException(REPEAT_ORDER, "repeatOrder", repeatOrderDto,
					ResourceException.ErrorType.CREATED, e);
		}

	}

	public AddressDto setOrderAddress(AddressDto addressDto) {
		AddressDto address = new AddressDto();
		address.setCity(addressDto.getCity());
		address.setCountry(addressDto.getCountry());
		address.setPinCode(addressDto.getPinCode());
		address.setState(addressDto.getState());
		address.setStreet(addressDto.getStreet());
		return address;
	}
	
	public Double getOptInIdAndPlaceOrder(RepeatOrderDto repeatOrderDto,String bearerToken) {
		Double price = 0D;
		OrdersDto order = new OrdersDto();
		Set<ItemDto> itemDtoSet = new HashSet<>();
		for (ProductDto product : repeatOrderDto.getRepeatOrderItems()) {
			List<ItemDto> items = getItemByProductId(bearerToken, product);
			for (ItemDto item : items) {
				if (item.getItemIdFk() != null) {
					price += item.getPrice() * item.getItemQuantity();
					itemDtoSet.add(item);
				}
			}
		}
		if (price > 0) {
			order.setItems(itemDtoSet);
			order.setOrderAddress(setOrderAddress(repeatOrderDto.getAddress()));
			order.setPrice(price);
			order.setUserId(repeatOrderDto.getUserId());
			order.setQuantity(Long.valueOf(itemDtoSet.size()));
		}
		try {
			CartDto cartDto = new CartDto(order.getUserId(), order.getItems(), order.getQuantity(), order.getPrice());
			 storeClient.buyCart(bearerToken,cartDto);
			 ordersService.createOrder(order);
			repeatOrderDto.setNumberOfDeliveries(repeatOrderDto.getNumberOfDeliveries() - 1);
			if(repeatOrderDto.getNumberOfDeliveries()>0) {
				repeatOrderDto.setDeliveryDate(repeatOrderDto.getDeliveryDate().plusDays(repeatOrderDto.getIntervalInDays()));
			}
			else {
				repeatOrderDto.setDeliveryDate(repeatOrderDto.getDeliveryDate());
			}
				updateOptIn(repeatOrderDto);
			return price;
		} catch (Exception e) {
			throw new ResourceException("Order", "order", order, ResourceException.ErrorType.CREATED, e);
		}
	}

	@Override
	public void getOptInToSendNotification(LocalDate date) {
		
		List<EmailDto> emailList = new ArrayList<>();
		String token;
		List<RepeatOrderDto> repeatOrderDtoList = new ArrayList<>();
		List<RepeatOrder> allRepeatOrders = repeatOrderRepository.findAllByDeliveryDate(date.plusDays(3));
		for (RepeatOrder repeatOrder : allRepeatOrders) {
			repeatOrderDtoList.add(modelMapper.map(repeatOrder, RepeatOrderDto.class));
		}
		if(!repeatOrderDtoList.isEmpty()) {
			LoginRequest login = new LoginRequest();
			login.setUsername("admin1");
			login.setPassword("admin");
			ResponseEntity<JwtResponse> response = authClient.authenticateUser(login);
			token = response.getBody().getType() + " " + response.getBody().getToken();
		
		for (RepeatOrderDto repeatOrder : repeatOrderDtoList) {
			Double totalPrice = 0D;
			if(repeatOrder.getNumberOfDeliveries()>0) {
				totalPrice=getOptInIdAndPlaceOrder(repeatOrder,token);
				if (totalPrice > 0) {
					EmailDto emailDto = new EmailDto(repeatOrder.getUserId(), "Your OptIn", "Your order has been placed successfully, the total price is " + totalPrice+" for optIn name "+repeatOrder.getName());
					emailList.add(emailDto);
				}
			}
		}
		if (!emailList.isEmpty()) {
			emailClient.sendBulkEmail(token,emailList);
			
		}
		authClient.logout(token);
		}
		
	}
	public List<ItemDto> getItemByProductId(String token, ProductDto productDto) {
		Long quantity = productDto.getQuantity();
		List<ItemDto> items = new ArrayList<>();
		ResponseEntity<PageableResponse<ItemDto>> result = null;
		int i = 0;
		while (quantity > 0) {
			if (token != null) {
				result = itemClient.getItemsByProductIdWithHeader(token, i, 1, productDto.getProductIdFk());
			} else {
				result = itemClient.getItemsByProductId(i, 1, productDto.getProductIdFk());
			}
			PageableResponse<ItemDto> res = result.getBody();
			if (res.getData() == null) {
				throw new ResourceException("Product", "product", productDto, ResourceException.ErrorType.FOUND);
			} else {
				for (ItemDto obj : res.getData()) {
					ItemDto item = new ItemDto();
					if (quantity > obj.getItemQuantity()) {
						item.setItemIdFk(obj.getItemId());
						item.setPrice(obj.getPrice());
						//item.setQuantity(obj.getItemQuantity().intValue());
						item.setItemQuantity(obj.getItemQuantity());
					} else {
						item.setItemIdFk(obj.getItemId());
						item.setPrice(obj.getPrice());
						//item.setQuantity(obj.getItemQuantity().intValue());
						item.setItemQuantity(quantity);
					}
					quantity -= item.getItemQuantity();
					items.add(item);
					i++;
				}

			}
		}
		return items;
	}

	@Override
	public PageableResponse<RepeatOrderDto> getAllOptInByUserId(Long userId, Integer pageNumber, Integer pageSize) {
		Pageable requestedPage = PageRequest.of(pageNumber, pageSize);
		Page<RepeatOrder> repeateOrdersPage = repeatOrderRepository.findAllByUserId(userId, requestedPage);
		List<RepeatOrderDto> repeatOrderDtoList = new ArrayList<>();
		for (RepeatOrder store : repeateOrdersPage.getContent())
			repeatOrderDtoList.add(modelMapper.map(store, RepeatOrderDto.class));
		PageableResponse<RepeatOrderDto> pageableRepeatOrder = new PageableResponse<>();
		return pageableRepeatOrder.setResponseData(repeatOrderDtoList, repeateOrdersPage);
	}

	@Override
	public RepeatOrderDto getOptInById(Long optinId) {
		Optional<RepeatOrder> optionalRepeatOrder = repeatOrderRepository.findById(optinId);
		if (optionalRepeatOrder.isEmpty())
			throw new ResourceException("Optin", "ID", optinId, ResourceException.ErrorType.FOUND);
		return modelMapper.map(optionalRepeatOrder.get(), RepeatOrderDto.class);
	}

	@Override
	public void deleteOptIn(Long optinId) {
		Optional<RepeatOrder> optionalOrders = repeatOrderRepository.findById(optinId);
		if (optionalOrders.isEmpty())
			throw new ResourceException("Order", "ID", optinId, ResourceException.ErrorType.FOUND);
		try {
			repeatOrderRepository.deleteById(optinId);
		} catch (Exception e) {
			throw new ResourceException("OptIn", "ID", optinId, ResourceException.ErrorType.DELETED, e);
		}
	}

	@Override
	public void deleteAllOptInByUserId(Long userId) {
		List<RepeatOrder> repeatOrderList=repeatOrderRepository.findAllByUserIdEquals(userId);
		try {
			repeatOrderRepository.deleteAll(repeatOrderList);
		}catch (Exception e) {
			throw new ResourceException("OptIn", "ID", repeatOrderList, ResourceException.ErrorType.DELETED, e);
		}
		
	}

}
