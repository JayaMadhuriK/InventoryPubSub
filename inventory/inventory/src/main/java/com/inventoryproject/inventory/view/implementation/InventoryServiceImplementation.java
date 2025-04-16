package com.inventoryproject.inventory.view.implementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventoryproject.inventory.model.ExceptionResponse;
import com.inventoryproject.inventory.model.Inventory;
import com.inventoryproject.inventory.pubsub.PubSubPublisherService;
import com.inventoryproject.inventory.repository.InventoryRepo;
import com.inventoryproject.inventory.view.InventoryService;

@Service
public class InventoryServiceImplementation implements InventoryService{
	
	@Autowired
	InventoryRepo inventoryRepo;
	@Autowired
	PubSubPublisherService pubSubPublisherService;
	
	@Override
	public boolean updateStock(List<Inventory> list) {
		System.out.println("inside update stock inventory list"+list);
		boolean response;
		try {
			inventoryRepo.saveAll(list);
		}catch(Exception e) {
			System.out.println("Error in update stock"+e.getMessage());
			response = false;
			return response;
		}
		System.out.println("after try catch in updatestock");
		response = true;
		pubSubPublisherService.publishUpdateStockResponse(response);
		return response;
	}

	@Override
	public List<Inventory> fetchQuantity(List<String> product_ids) {
		System.out.println("Inside fetchquantity inventory"+product_ids);
		List<Inventory> result = new ArrayList<>();
		try {
			result = inventoryRepo.findAllById(product_ids);
		    System.out.println("Inside fetchquantity inventory result"+result);
			pubSubPublisherService.publishInventoryResponse(result);
		}catch(Exception e) {
			System.out.println("Exception in fetchQuantity"+e.getMessage());
			e.printStackTrace();
		}
		
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
