package com.order.service;

import com.order.model.Order;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Order saveOrder(Order order);
    List<Order> saveOrders(List<Order> orders);
    List<Order> getOrders();
    Order getOrderById(UUID id);
    Order getOrderType(String name);
    String deleteOrder(UUID id);

    Order updateOrder(UUID orderId, int newQuantity);


}
