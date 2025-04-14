package com.inventoryproject.inventory.view.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventoryproject.inventory.model.ExceptionResponse;
import com.inventoryproject.inventory.model.Inventory;
import com.inventoryproject.inventory.pubsub.PubSubPublisherService;
import com.inventoryproject.inventory.pubsub.PubSubSubscriberService;
import com.inventoryproject.inventory.repository.InventoryRepo;
import com.inventoryproject.inventory.view.InventoryService;

@Service
public class InventoryServiceImplementation implements InventoryService{
	
	@Autowired
	InventoryRepo inventoryRepo;
	@Autowired
	PubSubPublisherService pubSubPublisherService;
	@Autowired
	PubSubSubscriberService pubSubSubscriberService;

	@Override
	public boolean updateStock() {
		CompletableFuture<List<Inventory>> result = pubSubSubscriberService.subscribeUpdateStock();
		List<Inventory> list = result.join();
		inventoryRepo.saveAll(list);
		pubSubPublisherService.publishUpdateStockResponse(true);
		return true;
	}

	@Override
	public List<Inventory> fetchQuantity() {
		CompletableFuture<List<String>> response = pubSubSubscriberService.subscribeInventory();
		List<String> product_ids = response.join();
		List<Inventory> result = new ArrayList<>();
		try {
			 result = inventoryRepo.findAllById(product_ids);
			 if(result.isEmpty()) {
				 throw new ExceptionResponse("Product ids does not exists");
			 }
		}
		catch(Exception e) {
			return result;
		}
		pubSubPublisherService.publishInventoryResponse(result);
		return result;
	}

	@Override
	public Object createProduct(Inventory inventory) {
		try {
			if(inventory.getProduct_id() == null || inventory.getProduct_id().isBlank()) {
				throw new ExceptionResponse("Invalid product id:"+inventory.getProduct_id());
			}else if(inventoryRepo.existsById(inventory.getProduct_id())) {
				throw new ExceptionResponse("product already exists with ID:"+inventory.getProduct_id());
			}else if(inventory.getStock() < 1) {
				throw new ExceptionResponse("Stock should not be less than one");
			}
		}
		catch(Exception e) {
			return e.getMessage();
		}
		return inventoryRepo.save(inventory);
	}

}
