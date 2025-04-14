package com.inventoryproject.order.view.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventoryproject.order.model.ExceptionResponse;
import com.inventoryproject.order.model.InventoryDto;
import com.inventoryproject.order.model.OrderItems;
import com.inventoryproject.order.model.OrderStatus;
import com.inventoryproject.order.model.Orders;
import com.inventoryproject.order.pubsub.PubSubPublisherService;
import com.inventoryproject.order.pubsub.PubSubSubscriberService;
import com.inventoryproject.order.repository.OrderItemsRepo;
import com.inventoryproject.order.repository.OrdersRepo;
import com.inventoryproject.order.view.OrdersService;

@Service
public class OrdersServiceImplementation implements OrdersService{

	@Autowired
	OrdersRepo ordersRepo;
	@Autowired
	OrderItemsRepo orderItemsRepo;
	@Autowired
	PubSubPublisherService pubSubPublisherService;
	@Autowired
	PubSubSubscriberService pubSubSubscriberService;
	
	
	@Override
	public Object createOrder(Orders orders) {
		try {
			if(orders.getUid() == null || orders.getUid().isBlank()) {
				throw new ExceptionResponse("invalid user id");
			}else {
				List<OrderItems> orderItems = orders.getOrderItems();
//				System.out.println("---------------------"+orders.getOrderItems().toString()+"------------------------------");
				if(orderItems == null || orderItems.isEmpty()) {
//					System.out.println("---------------------"+orders.getOrderItems().toString()+"------------------------------");
					orders.setStatus(OrderStatus.NON_SERVICEABLE);
			    	ordersRepo.save(orders);
			    	throw new ExceptionResponse("Atleast one product must have in cart");
				}
				orders.setStatus(OrderStatus.SERVICEABLE);
				Set<String> productIdsList = orders.getOrderItems().stream()
						.map(OrderItems::getProduct_id)
						.collect(Collectors.toSet());
				pubSubPublisherService.publishInventory(productIdsList);
				CompletableFuture<List<InventoryDto>> result = pubSubSubscriberService.subscribeInventoryResponse();
//				List<InventoryDto> fetchQuantity = webClient
//						.get()
//						.uri(builder -> builder
//						.path("/api/inventory/fetchproductids")
//						.queryParam("product_ids", productIdsList)
//						.build())
//						.retrieve()
//						.bodyToFlux(InventoryDto.class)
//						.collectList()
//						.block();
				List<InventoryDto> fetchQuantity = result.join();
				if(productIdsList.size() != fetchQuantity.size()) {
					orders.setStatus(OrderStatus.NON_SERVICEABLE);
					for(OrderItems item : orders.getOrderItems()) {
						item.setOrder(orders);
					}
					ordersRepo.save(orders);
					throw new ExceptionResponse("Product list contains unavailable items");
				}else {
					Map<String, Integer> productQtyList = orders.getOrderItems().stream()
							.collect(Collectors.groupingBy(OrderItems::getProduct_id
									,Collectors.summingInt(OrderItems::getQuantity)));
					List<InventoryDto> updatedList = new ArrayList<>();
					for (Map.Entry<String,Integer> requested : productQtyList.entrySet()) {
						String productId = requested.getKey();
						int requestedQty = requested.getValue();
					    InventoryDto match = fetchQuantity.stream()
					        .filter(inv -> inv.getProduct_id().equals(productId))
					        .findFirst()
					        .orElse(null);
					    if(requestedQty < 1) {
					    	orders.setStatus(OrderStatus.NON_SERVICEABLE);
					    	for(OrderItems item : orders.getOrderItems()) {
								item.setOrder(orders);
							}
					    	ordersRepo.save(orders);
					    	throw new ExceptionResponse("Quantity should be more than zero");
					    }else if (match == null || match.getStock() < requestedQty) {
					    	orders.setStatus(OrderStatus.NON_SERVICEABLE);
					    	for(OrderItems item : orders.getOrderItems()) {
								item.setOrder(orders);
							}
					    	ordersRepo.save(orders);
					    	throw new ExceptionResponse("Sufficient stock not available for "+productId);
					    }
			//		    if (match!=null && match.getStock() < requested.getQuantity()) {
				    	InventoryDto inventoryDto = new InventoryDto(productId,match.getStock() - requestedQty);
				    	updatedList.add(inventoryDto);
			//		    }
					}
					if(orders.getStatus() == OrderStatus.SERVICEABLE) {
						pubSubPublisherService.publishUpdateStock(updatedList);
						boolean response = pubSubSubscriberService.subscribeUpdateStockResponse();
//						boolean response = webClient.put()
//						.uri("/api/inventory/updatestock")
//						.bodyValue(updatedList)
//						.retrieve()
//						.bodyToMono(Boolean.class)
//						.block();
						System.out.println("------------------"+response+"-------------------");
						if(response == true) {
							for(OrderItems item : orders.getOrderItems()) {
								item.setOrder(orders);
							}
	//						System.out.println("------------------"+response+"-------------------");
							ordersRepo.save(orders);
						}
					}
				}
			}
		}catch(ExceptionResponse ex) {
			return ex.getMessage();
		}
		return orders;
	}

}
