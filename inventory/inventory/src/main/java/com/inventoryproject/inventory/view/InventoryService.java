package com.inventoryproject.inventory.view;

import java.util.List;

import com.inventoryproject.inventory.model.Inventory;

public interface InventoryService {
	Object createProduct(Inventory inventory);
	boolean updateStock(List<Inventory> list);
	List<Inventory> fetchQuantity(List<String> product_ids);
}
