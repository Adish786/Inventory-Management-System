package com.service.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import com.service.model.Inventory;
import com.service.model.Order;
import com.service.model.OrderStatus;
import com.service.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.service.repository.OrderRepository;


@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final ConcurrentMap<UUID, ReentrantLock> productLocks = new ConcurrentHashMap<>();
    public OrderServiceImpl(OrderRepository orderRepository, InventoryRepository inventoryRepository) {
        this.orderRepository = orderRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Cacheable("orders")
    public Order getOrderById(UUID id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public Order saveOrder(Order order) {
       return orderRepository.save(order);
    }

    @Override
    public List<Order> saveOrders(List<Order> orders) {
        return orderRepository.saveAll(orders);
    }

    @Override
    @Cacheable("orders")
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @Override
    @Cacheable("orders")
    public Order getOrderType(String orderType) {
        return orderRepository.findByOrderType(orderType);
    }

    @Override
    public String deleteOrder(UUID id) {
        orderRepository.deleteById(id);
        return "Order removed !! " + id;
    }

    private ReentrantLock getLock(UUID productId) {
        return productLocks.computeIfAbsent(productId, id -> new ReentrantLock());
    }

    @Transactional
    public Order placeOrder(UUID customerId, UUID productId, int quantity) {
        ReentrantLock lock = getLock(productId);
        lock.lock();
        try {
            Inventory inventory = inventoryRepository.findByProductId(productId)
                    .orElseThrow(() -> new IllegalStateException("Product not found"));

            if (inventory.getStockQuantity() < quantity) {
                throw new IllegalStateException("Not enough stock available");
            }

            Order order = new Order(customerId, productId, quantity, inventory.getPrice() * quantity);
            inventory.decreaseStock(quantity);

            orderRepository.save(order);
            inventoryRepository.save(inventory);

            return order;
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional
    public Order updateOrder(UUID orderId, int newQuantity) {
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
                throw new IllegalStateException("Not enough stock available");
            }

            order.updateOrder(newQuantity, inventory.getPrice() * newQuantity);
            inventory.decreaseStock(quantityDiff);

            orderRepository.save(order);
            inventoryRepository.save(inventory);
            return order;
        } finally {
            lock.unlock();
        }
    }

    @Transactional
    public void fulfillOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.PAID) {
            throw new IllegalStateException("Order is not paid yet");
        }

        order.markAsFulfilled();
        orderRepository.save(order);
    }

    @Transactional
    public void cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        order.cancelOrder();
        orderRepository.save(order);
    }
}
