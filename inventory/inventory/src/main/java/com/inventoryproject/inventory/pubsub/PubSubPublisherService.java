package com.inventoryproject.inventory.pubsub;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.gson.Gson;
import com.inventoryproject.inventory.model.Inventory;

@Service
public class PubSubPublisherService {
	@Autowired
	private PubSubTemplate pubSubTemplate;
	private final Gson gson = new Gson();
	
	public void publishInventoryResponse(List<Inventory> response) {
		String message = gson.toJson(response);
		pubSubTemplate.publish("inventory-response", message);
	}
	
	public void publishUpdateStockResponse(Boolean response) {
		String message = gson.toJson(response);
		pubSubTemplate.publish("update-response", message);
	}
}
