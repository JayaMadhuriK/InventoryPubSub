package com.inventoryproject.inventory.view.implementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventoryproject.inventory.model.ExceptionResponse;
import com.inventoryproject.inventory.model.Inventory;
import com.inventoryproject.inventory.repository.InventoryRepo;
import com.inventoryproject.inventory.view.InventoryService;

@Service
public class InventoryServiceImplementation implements InventoryService{
	
	@Autowired
	InventoryRepo inventoryRepo;

	@Override
	public boolean updateStock(List<Inventory> list) {
		inventoryRepo.saveAll(list);
		return true;
	}

	@Override
	public List<Inventory> fetchQuantity(List<String> product_ids) {
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
		return result;
	}

	@Override
	public Inventory createProduct(Inventory inventory) {
		if(inventoryRepo.existsById(inventory.getProduct_id())) {
			throw new ExceptionResponse("product already exists with ID:"+inventory.getProduct_id());
		}else if(inventory.getStock() < 1) {
			throw new ExceptionResponse("Stock should not be less than one");
		}
		return inventoryRepo.save(inventory);
	}

}
