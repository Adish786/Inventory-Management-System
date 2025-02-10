package com.order.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.order.model.Inventory;
import com.order.model.Order;
import com.order.model.OrderStatus;
import com.order.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import com.order.repository.OrderRepository;


@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository repository;
    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;

    public OrderServiceImpl(OrderRepository repository, OrderRepository orderRepository, InventoryRepository inventoryRepository) {
        this.repository = repository;
        this.orderRepository = orderRepository;
        this.inventoryRepository = inventoryRepository;
    }


    public Order saveOrder(Order order) {
        return repository.save(order);
    }

    public List<Order> saveOrders(List<Order> orders) {
        return repository.saveAll(orders);
    }

    public List<Order> getOrders() {
        return repository.findAll();
    }

    public Order getOrderById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public Order getOrderType(String orderType) {
        return repository.findByOrderType(orderType);
    }

    public String deleteOrder(UUID id) {
        repository.deleteById(id);
        return "Order removed !! " + id;
    }
/*
	@KafkaTemplate(topic = "stock_update", groupId = "inventory")
	public void sendStockUpdate(Long productId, int quantity) {
		kafkaTemplate.send("stock_update", new StockUpdateEvent(productId, quantity));
	}
 */

    @Transactional
    public Order placeOrder(UUID customerId, UUID productId, int quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new IllegalStateException("Product not found"));

        if (inventory.getStockQuantity() < quantity) {
            throw new IllegalStateException("Not enough stock available");
        }

        // Create order
        Order order = new Order(customerId, productId, quantity, inventory.getPrice() * quantity);
        inventory.decreaseStock(quantity);

        orderRepository.save(order);
        inventoryRepository.save(inventory);

        return order;
    }

    @Transactional
    public Order updateOrder(UUID orderId, int newQuantity) {
       Order order = orderRepository.findById(orderId).get();
      Inventory inventory = Optional.ofNullable(inventoryRepository.findByProductId(order.getProductId())).get().get();
        if (inventory.getStockQuantity() < newQuantity) {
            throw new IllegalStateException("Not enough stock available");
        }

        order.updateOrder(newQuantity, inventory.getPrice() * newQuantity);
        inventory.decreaseStock(newQuantity - order.getQuantity());
        orderRepository.save(order);
        inventoryRepository.save(inventory);
        return order;
    }

    @Transactional
    public void fulfillOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId).get();
        if (order.getOrderStatus() == OrderStatus.PAID) {
            order.markAsFulfilled();
            orderRepository.save(order);
        } else {
            throw new IllegalStateException("Order is not paid yet");
        }
    }

    @Transactional
    public void cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId).get();
        order.cancelOrder();
        orderRepository.save(order);
    }

}
