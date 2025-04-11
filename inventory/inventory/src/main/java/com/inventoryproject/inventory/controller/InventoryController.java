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
	public String updateStock(@RequestBody List<Inventory> list) {
		boolean response = inventoryService.updateStock(list);
		if(response != true) {
			return "Error in updation";
		}
		return "Successfully Updated";
	}
	@GetMapping("/fetchproductids")
	public List<Inventory> fetchQuantity(@RequestParam List<String> product_ids) {
		List<Inventory> inventoryList = inventoryService.fetchQuantity(product_ids);
		return inventoryList;
	}
	@PostMapping("/createproduct")
	public Inventory createProduct(@RequestBody Inventory inventory) {
		Inventory inv = inventoryService.createProduct(inventory);
		return inv;
	}

}
