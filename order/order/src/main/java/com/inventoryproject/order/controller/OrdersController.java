package com.inventoryproject.order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventoryproject.order.model.Orders;
import com.inventoryproject.order.view.OrdersService;

@RestController
@RequestMapping("/api/order")
public class OrdersController {

	@Autowired
	OrdersService orderService;
	
	@PostMapping("/createorder")
	public Object createOrder(@RequestBody Orders orders) {
		return orderService.createOrder(orders);
	}
	
	
}