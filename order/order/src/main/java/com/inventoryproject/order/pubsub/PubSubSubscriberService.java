package com.inventoryproject.order.pubsub;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.inventoryproject.order.model.InventoryDto;

@Component
public class PubSubSubscriberService {
	private final Gson gson = new Gson();
	@Autowired
	private PubSubTemplate pubSubTemplate;
	
	
	public CompletableFuture<List<InventoryDto>> subscribeInventoryResponse() {
		CompletableFuture<List<InventoryDto>> inventoryDtoFuture = new CompletableFuture<>();
		pubSubTemplate.subscribe("inventory-response-sub", message -> {
			String payload = message.getPubsubMessage().getData().toStringUtf8();
			List<InventoryDto> list = gson.fromJson(payload, new TypeToken<List<InventoryDto>>(){}.getType());
			System.out.println("stock updates"+payload);
			inventoryDtoFuture.complete(list);
			message.ack();
		});
		return inventoryDtoFuture;
	}
	
	
	public boolean subscribeUpdateStockResponse() {
		CompletableFuture<Boolean> inventoryDtoFuture = new CompletableFuture<>();
		pubSubTemplate.subscribe("update-response-sub", message -> {
			String payload = message.getPubsubMessage().getData().toStringUtf8();
			boolean response = Boolean.parseBoolean(payload);
			System.out.println("stock updates"+payload);
			inventoryDtoFuture.complete(response);
			message.ack();
		});
		try {
			return inventoryDtoFuture.get();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
	}
}