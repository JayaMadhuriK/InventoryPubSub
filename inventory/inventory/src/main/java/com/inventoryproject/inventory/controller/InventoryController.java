package com.inventoryproject.inventory.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventoryproject.inventory.model.Inventory;
import com.inventoryproject.inventory.view.InventoryService;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
	
	@Autowired
	InventoryService inventoryService;
	
	@PutMapping("/updatestock")
	public boolean updateStock(@RequestBody List<Inventory> list) {
		return inventoryService.updateStock();
	}
	@GetMapping("/fetchproductids")
	public List<Inventory> fetchQuantity(@RequestParam List<String> product_ids) {
		List<Inventory> inventoryList = inventoryService.fetchQuantity();
		return inventoryList;
	}
	@PostMapping("/createproduct")
	public Object createProduct(@RequestBody Inventory inventory) {
		Object inv = inventoryService.createProduct(inventory);
		return inv;
	}

}
