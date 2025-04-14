package com.inventoryproject.inventory.pubsub;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.inventoryproject.inventory.model.Inventory;

@Component
public class PubSubSubscriberService {
	private final Gson gson = new Gson();
	@Autowired
	private PubSubTemplate pubSubTemplate;
	
	public CompletableFuture<List<Inventory>> subscribeUpdateStock() {
		CompletableFuture<List<Inventory>> inventoryDtoFuture = new CompletableFuture<>();
		pubSubTemplate.subscribe("updateStock-sub", message -> {
			String payload = message.getPubsubMessage().getData().toStringUtf8();
			List<Inventory> response = gson.fromJson(payload, new TypeToken<List<Inventory>>(){}.getType());
			System.out.println("stock updates"+payload);
			inventoryDtoFuture.complete(response);
			message.ack();
		});
		return inventoryDtoFuture;
	}
	
	public CompletableFuture<List<String>> subscribeInventory() {
		CompletableFuture<List<String>> inventoryDtoFuture = new CompletableFuture<>();
		pubSubTemplate.subscribe("inventory-sub", message -> {
			String payload = message.getPubsubMessage().getData().toStringUtf8();
			List<String> response = gson.fromJson(payload, new TypeToken<List<String>>(){}.getType());
			System.out.println("stock updates"+payload);
			inventoryDtoFuture.complete(response);
			message.ack();
		});
		return inventoryDtoFuture;
	}
}