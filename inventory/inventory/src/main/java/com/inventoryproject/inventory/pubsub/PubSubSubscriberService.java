package com.inventoryproject.inventory.pubsub;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.inventoryproject.inventory.model.Inventory;
import com.inventoryproject.inventory.view.InventoryService;

@Component
public class PubSubSubscriberService {
	private final Gson gson = new Gson();
	@Autowired
	private PubSubTemplate pubSubTemplate;
	@Autowired
	InventoryService inventoryService;
	
	@PostConstruct
	public void init() {
		subscribeUpdateStock();
		subscribeInventory();
		subscribeUpdateStockDlq();
		subscribeInventoryDlq();
	}
	
	
	public void subscribeUpdateStockDlq() {
		pubSubTemplate.subscribe("updateStock-dlq-sub", message -> {
			String payload = message.getPubsubMessage().getData().toStringUtf8();
				List<Inventory> response = gson.fromJson(payload, new TypeToken<List<Inventory>>(){}.getType());
				System.out.println("message sent to dlq inventory"+response);
				inventoryService.updateStock(response);
				message.ack();
		});
	}
	public void subscribeUpdateStock() {
		System.out.println("calling subscribeupdatestock inventory");
		pubSubTemplate.subscribe("updateStock-sub", message -> {
			String payload = message.getPubsubMessage().getData().toStringUtf8();
			try {
				System.out.println("inside subscribe update stock inventory payload"+payload);
				List<Inventory> response = gson.fromJson(payload, new TypeToken<List<Inventory>>(){}.getType());
				System.out.println("inside subscribe update stock inventory response"+response);
				inventoryService.updateStock(response);
				message.ack();
			}catch(Exception e) {
				System.out.println("Error processing inventory message"+e.getMessage());
				message.nack();
			}
		});
	}
	
	public void subscribeInventoryDlq() {
		pubSubTemplate.subscribe("inventory-dlq-sub", message -> {
		String payload = message.getPubsubMessage().getData().toStringUtf8();
		System.out.println("from dlq paylod"+payload);
		List<String> response = null;
		try {
			 response = gson.fromJson(payload, new TypeToken<List<String>>(){}.getType());
		}catch(JsonSyntaxException e) {
			try {
				System.out.println("response in subscriber"+response);
				String inner = gson.fromJson(payload, String.class);
				response = gson.fromJson(inner, new TypeToken<List<String>>(){}.getType());
				System.out.println("response in subscriber after inner"+response);
			}catch(Exception ex) {
				System.err.println("Error in processing"+ex.getMessage());
				message.nack();
				return;
			}
		}
		inventoryService.fetchQuantity(response);
		message.ack();
		});
	}
	
	public void subscribeInventory() {
		System.out.println("Inside subscribe inventory pubsubpublisherservice inventory");
		pubSubTemplate.subscribe("inventory-sub", message -> {
			String payload = message.getPubsubMessage().getData().toStringUtf8();
			System.out.println("Inside subscribe inventory pubsubpublisherservice inventory payload"+payload);
			try {
				List<String> response = gson.fromJson(payload, new TypeToken<List<String>>(){}.getType());
				System.out.println("Inside subscribe inventory pubsubpublisherservice inventory response"+response);
				inventoryService.fetchQuantity(response);
				message.ack();
			}catch(Exception e) {
				System.err.println("Error in processing"+e.getMessage());
				message.nack();
			}
		});
	}
}