package com.inventoryproject.order.view;

import java.util.List;

import com.inventoryproject.order.model.InventoryDto;
import com.inventoryproject.order.model.Orders;

public interface OrdersService {
	Object createOrder(Orders orders);
	List<InventoryDto> getInventoryDto();
	void addInventoryDto(List<InventoryDto> list);
	boolean getUpdateStockResponse();
	void addUpdateStockResponse(boolean result);
}
