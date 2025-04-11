package com.inventoryproject.order.model;

public class ProductQtyDto {
	 private String product_id;
	 private int quantity;
	public String getProduct_id() {
		return product_id;
	}
	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public ProductQtyDto(String product_id, int quantity) {
		super();
		this.product_id = product_id;
		this.quantity = quantity;
	}
}
