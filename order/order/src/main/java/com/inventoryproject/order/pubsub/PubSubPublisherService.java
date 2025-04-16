package com.inventoryproject.order.pubsub;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.gson.Gson;
import com.inventoryproject.order.model.InventoryDto;

@Service
public class PubSubPublisherService {
	@Autowired
	private PubSubTemplate pubSubTemplate;
	private final Gson gson = new Gson();
	
	public void publishInventory(Set<String> productIds) {
		System.out.println("Inside publish inventory pubsubpublisherservice"+productIds);
		String message = gson.toJson(productIds);
		pubSubTemplate.publish("inventory", message);
		System.out.println("Inside publish inventory message pubsubpublisherservice"+message);
	}
	public void publishUpdateStock(List<InventoryDto> stockUpdates) {
		System.out.println("inside publishupdatestock order stockupdates"+stockUpdates);
		String message = gson.toJson(stockUpdates);
		System.out.println("inside publishupdatestock order before publish message"+message);
		pubSubTemplate.publish("updateStock", message);
	}
}
