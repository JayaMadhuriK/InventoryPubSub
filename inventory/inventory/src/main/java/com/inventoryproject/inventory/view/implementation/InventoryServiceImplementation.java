package com.inventoryproject.inventory.view.implementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		return false;
	}

	@Override
	public List<Inventory> fetchQuantity(List<String> product_ids) {
		List<Inventory> result = new ArrayList<>();
		try {
			 result = inventoryRepo.findAllById(product_ids);
		}
		catch(Exception e) {
			return result;
		}
		return result;
	}

	@Override
	public Inventory createProduct(Inventory inventory) {
		if(inventoryRepo.existsById(inventory.getProduct_id())) {
			throw new RuntimeException("product id already exists");
		}
		return inventoryRepo.save(inventory);
	}

}
