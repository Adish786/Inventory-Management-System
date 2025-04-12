package com.service.repository;

import com.service.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface OrderRepository extends JpaRepository<Order,UUID> {
	Order findByOrderType(String orderType);

	//Order findById(UUID orderId);
}
