package com.inventoryproject.order.view.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.inventoryproject.order.model.InventoryDto;
import com.inventoryproject.order.model.OrderItems;
import com.inventoryproject.order.model.OrderStatus;
import com.inventoryproject.order.model.Orders;
import com.inventoryproject.order.model.ProductQtyDto;
import com.inventoryproject.order.repository.OrderItemsRepo;
import com.inventoryproject.order.repository.OrdersRepo;
import com.inventoryproject.order.view.OrdersService;

@Service
public class OrdersServiceImplementation implements OrdersService{

	@Autowired
	OrdersRepo ordersRepo;
	@Autowired
	OrderItemsRepo orderItemsRepo;
	private final WebClient webClient;
	
	public OrdersServiceImplementation(WebClient.Builder webClientBuilder) {
		this.webClient = webClientBuilder
				.baseUrl("http://localhost:6001")
				.build();
	}
	
	@Override
	public Orders createOrder(Orders orders) {
		List<String> productIdsList = orders.getOrderItems().stream()
				.map(OrderItems::getProduct_id)
				.collect(Collectors.toList());
		List<InventoryDto> fetchQuantity = webClient
				.get()
				.uri(builder -> builder
				.path("/api/inventory/fetchproductids")
				.queryParam("product_ids", productIdsList)
				.build())
				.retrieve()
				.bodyToFlux(InventoryDto.class)
				.collectList()
				.block();
		List<ProductQtyDto> productQtyList = orders.getOrderItems().stream()
				.map(item -> new ProductQtyDto(item.getProduct_id(), item.getQuantity()))
				.collect(Collectors.toList());
		orders.setStatus(OrderStatus.SERVICEABLE);
		List<InventoryDto> updatedList = new ArrayList<>();
		for (ProductQtyDto requested : productQtyList) {
		    InventoryDto match = fetchQuantity.stream()
		        .filter(inv -> inv.getProduct_id().equals(requested.getProduct_id()))
		        .findFirst()
		        .orElse(null);
		    if (match == null || match.getStock() < requested.getQuantity()) {
		    	orders.setStatus(OrderStatus.NON_SERVICEABLE);
		    	ordersRepo.save(orders);
		    	break;
		    }
		    if (match!=null && match.getStock() < requested.getQuantity()) {
		    	InventoryDto inventoryDto = new InventoryDto(match.getProduct_id(),match.getStock() - requested.getQuantity());
		    	updatedList.add(inventoryDto);
		    }
		}
		String response = webClient.put()
		.uri("/api/inventory/updatestock")
		.bodyValue(updatedList)
		.retrieve()
		.bodyToMono(String.class)
		.block();
		if(response == "Successfully Updated") {
			ordersRepo.save(orders);
		}
		return orders;
	}

}
