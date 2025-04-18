package com.service.service;

import com.service.model.Order;

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
    void fulfillOrder(UUID orderId);
    void cancelOrder(UUID orderId);
    Order placeOrder(UUID customerId, UUID productId, int quantity);


}
