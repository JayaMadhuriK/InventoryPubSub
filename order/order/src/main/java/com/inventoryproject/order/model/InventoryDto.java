package com.inventoryproject.order.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InventoryDto {
	private String product_id;
	private int stock;
	public String getProduct_id() {
		return product_id;
	}
	public InventoryDto(String product_id, int stock) {
		super();
		this.product_id = product_id;
		this.stock = stock;
	}
	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}
	public int getStock() {
		return stock;
	}
	public void setStock(int stock) {
		this.stock = stock;
	}
}
