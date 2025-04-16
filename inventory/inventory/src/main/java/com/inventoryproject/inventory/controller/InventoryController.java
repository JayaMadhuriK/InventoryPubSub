package com.inventoryproject.inventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventoryproject.inventory.model.Inventory;
import com.inventoryproject.inventory.view.InventoryService;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
	
	@Autowired
	InventoryService inventoryService;
	
	@PostMapping("/createproduct")
	public Object createProduct(@RequestBody Inventory inventory) {
		return inventoryService.createProduct(inventory);
	}

}
