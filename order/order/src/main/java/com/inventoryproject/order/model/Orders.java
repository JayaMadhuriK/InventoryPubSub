package com.inventoryproject.order.model;

import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Orders {
	@Id
	@GeneratedValue
	private UUID order_id;
	@Column(nullable = false)
	private String uid;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private OrderStatus status;
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<OrderItems> orderItems;
	
	public Orders() {
		if(order_id == null) {
			order_id = UUID.randomUUID();
		}
	}
	public OrderStatus getStatus() {
		return status;
	}
	public void setStatus(OrderStatus status) {
		this.status = status;
	}
	public UUID getOrder_id() {
		return order_id;
	}
	public void setOrder_id(UUID order_id) {
		this.order_id = order_id;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public List<OrderItems> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<OrderItems> orderItems) {
		this.orderItems = orderItems;
	}
}
