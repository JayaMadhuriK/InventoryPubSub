package com.inventoryproject.order.pubsub;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.inventoryproject.order.model.InventoryDto;
import com.inventoryproject.order.view.OrdersService;

@Component
public class PubSubSubscriberService {
	private final Gson gson = new Gson();
	@Autowired
	private PubSubTemplate pubSubTemplate;
	@Autowired
	OrdersService orderService;
	
	@PostConstruct
	public void init() {
		subscribeInventoryResponse();
		subscribeUpdateStockResponse();
		subscribeInventoryResponseDlq();
		subscribeUpdateStockResponseDlq();
	}
	
	public void subscribeInventoryResponse() {
		System.out.println("Inside subscribe inventory response order");
		pubSubTemplate.subscribe("inventory-response-sub", message -> {
			String payload = message.getPubsubMessage().getData().toStringUtf8();
			try {
				System.out.println("Inside subscribe inventory response order payload"+payload);
				List<InventoryDto> list = gson.fromJson(payload, new TypeToken<List<InventoryDto>>(){}.getType());
				System.out.println("Inside subscribe inventory response order list"+list);
				Thread.sleep(5000);
				orderService.addInventoryDto(list);
				message.ack();
			}catch(Exception e) {
				System.out.println("Error processing inventory response message"+e.getMessage());
				message.nack();
			}
		});
	}
	
	public void subscribeInventoryResponseDlq() {
		System.out.println("Inside subscribe inventory response dlq");
		pubSubTemplate.subscribe("inventory-response-dlq-sub", message -> {
			String payload = message.getPubsubMessage().getData().toStringUtf8();
			List<InventoryDto> list = gson.fromJson(payload, new TypeToken<List<InventoryDto>>(){}.getType());
			orderService.addInventoryDto(list);
			System.out.println("inventory list"+list);
			message.ack();
		});
	}
	
	public void subscribeUpdateStockResponse() {
		System.out.println("Inside subscribe updatestcok response order");
		pubSubTemplate.subscribe("update-response-sub", message -> {
			String payload = message.getPubsubMessage().getData().toStringUtf8();
			try {
				System.out.println("Inside subscribe updatestcok response payload"+payload);
				boolean response = Boolean.parseBoolean(payload);
				System.out.println("Inside subscribe updatestcok response response"+response);
				orderService.addUpdateStockResponse(response);
				message.ack();
			}catch(Exception e) {
				System.out.println("Error processing updatestock message"+e.getMessage());
				message.nack();
			}
		});
	}
	
	public void subscribeUpdateStockResponseDlq() {
		System.out.println("Inside subscribe update stock response dlq");
		pubSubTemplate.subscribe("update-response-dlq-sub", message -> {
			String payload = message.getPubsubMessage().getData().toStringUtf8();
			boolean response = Boolean.parseBoolean(payload);
			orderService.addUpdateStockResponse(response);
			System.out.println("stock updates"+response);
			message.ack();
		});
	}
}