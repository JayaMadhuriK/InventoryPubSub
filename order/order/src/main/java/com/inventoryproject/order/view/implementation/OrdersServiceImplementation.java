package com.inventoryproject.order.view.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventoryproject.order.model.ExceptionResponse;
import com.inventoryproject.order.model.InventoryDto;
import com.inventoryproject.order.model.OrderItems;
import com.inventoryproject.order.model.OrderStatus;
import com.inventoryproject.order.model.Orders;
import com.inventoryproject.order.pubsub.PubSubPublisherService;
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
	
	private final List<InventoryDto> inventoryListStore = new CopyOnWriteArrayList<>();
	private final AtomicBoolean updateResult = new AtomicBoolean(false);
	
	@Override
	public Object createOrder(Orders orders) {
		System.out.println("Calling createOrder orderserviceimpl");
		try {
			if(orders.getUid() == null || orders.getUid().isBlank()) {
				throw new ExceptionResponse("invalid user id");
			}else {
				List<OrderItems> orderItems = orders.getOrderItems();
				if(orderItems == null || orderItems.isEmpty()) {
					orders.setStatus(OrderStatus.NON_SERVICEABLE);
			    	ordersRepo.save(orders);
			    	throw new ExceptionResponse("Atleast one product must have in cart");
				}
				orders.setStatus(OrderStatus.SERVICEABLE);
				Set<String> productIdsList = orders.getOrderItems().stream()
						.map(OrderItems::getProduct_id)
						.collect(Collectors.toSet());
				System.out.println("productIds list before publishing orderservcieimpl"+productIdsList);
				pubSubPublisherService.publishInventory(productIdsList);
				try {
					Thread.sleep(15000);
				} catch (Exception e) {
					System.out.println("Exception in adding");
					e.printStackTrace();
				}
				List<InventoryDto> fetchQuantity = getInventoryDto();
				System.out.println("orderservice impl getting list from getInventorydto"+getInventoryDto());
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
					    	System.out.println("requestedqty orderserviceimpl"+requestedQty);
					    	orders.setStatus(OrderStatus.NON_SERVICEABLE);
					    	for(OrderItems item : orders.getOrderItems()) {
								item.setOrder(orders);
							}
					    	ordersRepo.save(orders);
					    	throw new ExceptionResponse("Quantity should be more than zero");
					    }else if (match == null || match.getStock() < requestedQty) {
					    	System.out.println("orderservice impl if stock is less than qty"+(match.getStock() < requestedQty));
					    	orders.setStatus(OrderStatus.NON_SERVICEABLE);
					    	for(OrderItems item : orders.getOrderItems()) {
								item.setOrder(orders);
							}
					    	ordersRepo.save(orders);
					    	throw new ExceptionResponse("Sufficient stock not available for "+productId);
					    }
				    	InventoryDto inventoryDto = new InventoryDto(productId,match.getStock() - requestedQty);
				    	System.out.println("after deducting stock inventory values"+inventoryDto);
				    	updatedList.add(inventoryDto);
					}
					System.out.println("status of order"+orders.getStatus());
					if(orders.getStatus() == OrderStatus.SERVICEABLE) {
						System.out.println("before publishing to updatestock"+updatedList);
						pubSubPublisherService.publishUpdateStock(updatedList);
						boolean response = getUpdateStockResponse();
						System.out.println("after subscribe to updatestock"+response);
						if(response == true) {
							for(OrderItems item : orders.getOrderItems()) {
								item.setOrder(orders);
							}
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

	@Override
	public List<InventoryDto> getInventoryDto() {
		System.out.println("callin get inventory dto in orderservic impl"+inventoryListStore);
		return inventoryListStore;
	}

	@Override
	public void addInventoryDto(List<InventoryDto> list) {
		System.out.println("callin add dto in orderservic impl"+list);
		try {
			inventoryListStore.clear();
			inventoryListStore.addAll(new ArrayList<>(list));
			System.out.println("Calling add dto in list 2"+inventoryListStore);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean getUpdateStockResponse() {
		System.out.println("callin get update stock in orderservic impl");
		return updateResult.get();
	}

	@Override
	public void addUpdateStockResponse(boolean result) {
		System.out.println("callin add update stock in orderservic impl"+result);
		updateResult.set(result);
	}

}
