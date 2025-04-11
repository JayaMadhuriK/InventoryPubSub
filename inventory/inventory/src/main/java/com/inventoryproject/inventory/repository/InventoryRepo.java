package com.inventoryproject.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventoryproject.inventory.model.Inventory;

@Repository
public interface InventoryRepo extends JpaRepository<Inventory, String>{
	
}
