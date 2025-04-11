package com.inventoryproject.order.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventoryproject.order.model.Orders;

@Repository
public interface OrdersRepo extends JpaRepository<Orders, UUID>{

}
