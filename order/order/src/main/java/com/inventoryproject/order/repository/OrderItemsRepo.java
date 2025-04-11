package com.inventoryproject.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventoryproject.order.model.OrderItems;

@Repository
public interface OrderItemsRepo extends JpaRepository<OrderItems, Long>{

}
