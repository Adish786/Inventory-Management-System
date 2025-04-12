package com.service.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.service.model.Inventory;
import com.service.model.Order;
import com.service.model.OrderStatus;
import com.service.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.service.repository.OrderRepository;


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


    @Cacheable
    public Order saveOrder(Order order) {
        return repository.save(order);
    }

    @Cacheable
    public List<Order> saveOrders(List<Order> orders) {
        return repository.saveAll(orders);
    }

    @Cacheable
    public List<Order> getOrders() {
        return repository.findAll();
    }
@Cacheable
    public Order getOrderById(UUID id) {
        return repository.findById(id).orElse(null);
    }
@Cacheable
    public Order getOrderType(String orderType) {
        return repository.findByOrderType(orderType);
    }
@Cacheable
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
    @Cacheable("customers")
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
    @Cacheable("customers")
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
    @Cacheable("order")
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
    @Cacheable("order")
    public void cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId).get();
        order.cancelOrder();
        orderRepository.save(order);
    }

}
