package com.inventoryproject.inventory.model;

public class ExceptionResponse extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public ExceptionResponse(String message) {
		super(message);
	}

}
