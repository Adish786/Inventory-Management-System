package com.order.repository;

import com.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface OrderRepository extends JpaRepository<Order,UUID> {
	Order findByOrderType(String orderType);

	//Order findById(UUID orderId);
}
