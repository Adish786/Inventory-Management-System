package com.service.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import com.service.model.Inventory;
import com.service.model.Order;
import com.service.model.OrderStatus;
import com.service.repository.InventoryRepository;
import jakarta.annotation.PreDestroy;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.service.repository.OrderRepository;


@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final KafkaProducerService kafkaProducerService;

    private final ConcurrentMap<UUID, ReentrantLock> productLocks = new ConcurrentHashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    public OrderServiceImpl(OrderRepository orderRepository,
                            InventoryRepository inventoryRepository,
                            KafkaProducerService kafkaProducerService) {
        this.orderRepository = orderRepository;
        this.inventoryRepository = inventoryRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    private ReentrantLock getLock(UUID productId) {
        return productLocks.computeIfAbsent(productId, id -> new ReentrantLock());
    }

    @Override
    @Cacheable("orders")
    public Order getOrderById(UUID id) {
        log.info("Fetching order with ID: {}", id);
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public Order saveOrder(Order order) {
        log.info("Saving order: {}", order);
        return orderRepository.save(order);
    }

    @Override
    public List<Order> saveOrders(List<Order> orders) {
        log.info("Saving {} orders", orders.size());
        return orderRepository.saveAll(orders);
    }

    @Override
    @Cacheable("orders")
    public List<Order> getOrders() {
        log.info("Fetching all orders");
        return orderRepository.findAll();
    }

    @Override
    @Cacheable("orders")
    public Order getOrderType(String orderType) {
        log.info("Fetching order by type: {}", orderType);
        return orderRepository.findByOrderType(orderType);
    }

    @Override
    public String deleteOrder(UUID id) {
        log.warn("Deleting order with ID: {}", id);
        orderRepository.deleteById(id);
        return "Order removed !! " + id;
    }

    @Override
    @Transactional
    public Order placeOrder(UUID customerId, UUID productId, int quantity) {
        ReentrantLock lock = getLock(productId);
        lock.lock();
        try {
            log.info("Placing order for customer {} for product {} (qty: {})", customerId, productId, quantity);
            Inventory inventory = inventoryRepository.findByProductId(productId)
                    .orElseThrow(() -> new IllegalStateException("Product not found"));

            if (inventory.getStockQuantity() < quantity) {
                log.error("Insufficient stock for product {}: requested {}, available {}", productId, quantity, inventory.getStockQuantity());
                throw new IllegalStateException("Not enough stock available");
            }

            Order order = new Order(customerId, productId, quantity, inventory.getPrice() * quantity);
            inventory.decreaseStock(quantity);

            Order savedOrder = orderRepository.save(order);
            inventoryRepository.save(inventory);

            // Async Kafka notification
            executor.submit(() -> kafkaProducerService.sendMessage("order_events", "OrderPlaced: " + savedOrder.getId()));

            return savedOrder;
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional
    public Order updateOrder(UUID orderId, int newQuantity) {
        log.info("Updating order {} to quantity {}", orderId, newQuantity);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        UUID productId = order.getProductId();
        ReentrantLock lock = getLock(productId);
        lock.lock();
        try {
            Inventory inventory = inventoryRepository.findByProductId(productId)
                    .orElseThrow(() -> new IllegalStateException("Product not found"));

            int quantityDiff = newQuantity - order.getQuantity();

            if (inventory.getStockQuantity() < quantityDiff) {
                log.error("Not enough inventory to increase quantity for order {}", orderId);
                throw new IllegalStateException("Not enough stock available");
            }

            order.updateOrder(newQuantity, inventory.getPrice() * newQuantity);
            inventory.decreaseStock(quantityDiff);

            Order updatedOrder = orderRepository.save(order);
            inventoryRepository.save(inventory);

            // Async Kafka notification
            executor.submit(() -> kafkaProducerService.sendMessage("order_events", "OrderUpdated: " + updatedOrder.getId()));

            return updatedOrder;
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional
    public void fulfillOrder(UUID orderId) {
        log.info("Fulfilling order {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.PAID) {
            log.warn("Attempted to fulfill unpaid order: {}", orderId);
            throw new IllegalStateException("Order is not paid yet");
        }

        order.markAsFulfilled();
        orderRepository.save(order);

        executor.submit(() -> kafkaProducerService.sendMessage("order_events", "OrderFulfilled: " + orderId));
    }

    @Override
    @Transactional
    public void cancelOrder(UUID orderId) {
        log.info("Cancelling order {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.cancelOrder();
        orderRepository.save(order);

        executor.submit(() -> kafkaProducerService.sendMessage("order_events", "OrderCancelled: " + orderId));
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down OrderServiceImpl executor...");
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("OrderServiceImpl executor shutdown interrupted", e);
            executor.shutdownNow();
        }
    }
}
