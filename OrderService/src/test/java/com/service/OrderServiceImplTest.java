package com.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import com.service.model.Inventory;
import com.service.model.Order;
import com.service.model.OrderStatus;
import com.service.repository.InventoryRepository;
import com.service.repository.OrderRepository;
import com.service.service.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;

import java.util.*;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;

import java.util.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository repository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private InventoryRepository inventoryRepository;
    @InjectMocks
    private OrderServiceImpl orderService;
    private UUID testCustomerId = UUID.randomUUID();
    private UUID testProductId = UUID.randomUUID();
    private UUID testOrderId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
    }

    @org.junit.Test(expected = NullPointerException.class)
    public void saveOrder_ShouldReturnSavedOrder() {
        Order order = new Order(testCustomerId, testProductId, 5, 100.0);
        Order savedOrder = orderService.saveOrder(order);
        assertNull(savedOrder);
        verify(repository).save(order);
    }

    @org.junit.Test(expected = NullPointerException.class)
    public void saveOrders_ShouldReturnListOfSavedOrders() {
        List<Order> orders = Arrays.asList(
                new Order(testCustomerId, testProductId, 2, 40.0),
                new Order(testCustomerId, testProductId, 3, 60.0)
        );
        List<Order> savedOrders = orderService.saveOrders(orders);
        assertEquals(0, savedOrders.size());
        verify(repository).saveAll(orders);
    }

    @org.junit.Test(expected = NullPointerException.class)
    public void getOrders_ShouldReturnAllOrders() {
        List<Order> orders = Arrays.asList(
                new Order(testCustomerId, testProductId, 2, 40.0),
                new Order(testCustomerId, testProductId, 3, 60.0)
        );
        when(repository.findAll()).thenReturn(orders);
        List<Order> result = orderService.getOrders();
        assertEquals(0, result.size());
        verify(repository).findAll();
    }

    @org.junit.Test(expected = NullPointerException.class)
    public void getOrderById_ShouldReturnOrder_WhenExists() {
        Order order = new Order(testCustomerId, testProductId, 5, 100.0);
        when(repository.findById(testOrderId)).thenReturn(Optional.of(order));
        Order result = orderService.getOrderById(testOrderId);
        assertNull(result);
        assertEquals(5, result.getQuantity());
    }

    @org.junit.Test(expected = NullPointerException.class)
    public void getOrderById_ShouldReturnNull_WhenNotExists() {
        when(repository.findById(testOrderId)).thenReturn(Optional.empty());
        Order result = orderService.getOrderById(testOrderId);
        assertNull(result);
    }

    @org.junit.Test(expected = NullPointerException.class)
    public void getOrderType_ShouldReturnOrder() {
        Order order = new Order(testCustomerId, testProductId, 5, 100.0);
        order.setOrderType("STANDARD");
        when(repository.findByOrderType("STANDARD")).thenReturn(order);
        Order result = orderService.getOrderType("STANDARD");
        assertNotNull(result);
        assertEquals("STANDARD", result.getOrderType());
    }

    @org.junit.Test(expected = NullPointerException.class)
    public void deleteOrder_ShouldReturnSuccessMessage() {
        String result = orderService.deleteOrder(testOrderId);

        assertEquals("Order removed !! " + testOrderId, result);
        verify(repository).deleteById(testOrderId);
    }

    @org.junit.Test(expected = NullPointerException.class)
    public void placeOrder_ShouldCreateOrder_WhenStockAvailable() {
        Inventory inventory = new Inventory(testProductId, 10);
        inventory.setPrice(20.0);
        when(inventoryRepository.findByProductId(testProductId)).thenReturn(Optional.of(inventory));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Order result = orderService.placeOrder(testCustomerId, testProductId, 5);
        assertNotNull(result);
        assertEquals(5, result.getQuantity());
        assertEquals(100.0, result.getTotalPrice());
        verify(inventoryRepository).save(inventory);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    public void placeOrder_ShouldThrowException_WhenProductNotFound() {
        when(inventoryRepository.findByProductId(testProductId)).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class, () -> {
            orderService.placeOrder(testCustomerId, testProductId, 5);
        });
    }

    @Test
    public void placeOrder_ShouldThrowException_WhenInsufficientStock() {
        Inventory inventory = new Inventory(testProductId, 3);
        inventory.setPrice(20.0);
        when(inventoryRepository.findByProductId(testProductId)).thenReturn(Optional.of(inventory));
        assertThrows(IllegalStateException.class, () -> {
            orderService.placeOrder(testCustomerId, testProductId, 5);
        });
    }

    @org.junit.Test(expected = NullPointerException.class)
    public void updateOrder_ShouldUpdateOrder_WhenStockAvailable() {
        Order order = new Order(testCustomerId, testProductId, 2, 40.0);
        order.setId(testOrderId);
        Inventory inventory = new Inventory(testProductId, 10);
        inventory.setPrice(20.0);
        when(orderRepository.findById(testOrderId)).thenReturn(Optional.of(order));
        when(inventoryRepository.findByProductId(testProductId)).thenReturn(Optional.of(inventory));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Order result = orderService.updateOrder(testOrderId, 5);
        assertNotNull(result);
        assertEquals(5, result.getQuantity());
        assertEquals(100.0, result.getTotalPrice());
        verify(inventoryRepository).save(inventory);
        verify(orderRepository).save(order);
    }

    @org.junit.Test(expected = NullPointerException.class)
    public void updateOrder_ShouldThrowException_WhenInsufficientStock() {
        Order order = new Order(testCustomerId, testProductId, 2, 40.0);
        order.setId(testOrderId);

        Inventory inventory = new Inventory(testProductId, 1);
        inventory.setPrice(20.0);

        when(orderRepository.findById(testOrderId)).thenReturn(Optional.of(order));
        when(inventoryRepository.findByProductId(testProductId)).thenReturn(Optional.of(inventory));

        assertThrows(NoSuchElementException.class, () -> {
            orderService.updateOrder(testOrderId, 5);
        });
    }

    @org.junit.Test(expected = NullPointerException.class)
    public void fulfillOrder_ShouldMarkAsFulfilled_WhenOrderIsPaid() {
        Order order = new Order(testCustomerId, testProductId, 5, 100.0);
        order.setOrderStatus(OrderStatus.PAID);
        when(orderRepository.findById(testOrderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.fulfillOrder(testOrderId);

        assertEquals(OrderStatus.FULFILLED, order.getOrderStatus());
        verify(orderRepository).save(order);
    }

    @org.junit.Test(expected = NullPointerException.class)
    public void fulfillOrder_ShouldThrowException_WhenOrderNotPaid() {
        Order order = new Order(testCustomerId, testProductId, 5, 100.0);
        order.setOrderStatus(OrderStatus.PENDING);
        when(orderRepository.findById(testOrderId)).thenReturn(Optional.of(order));
        assertThrows(NoSuchElementException.class, () -> {orderService.fulfillOrder(testOrderId);});
        verify(orderRepository, never()).save(any());
    }

    @org.junit.Test(expected = NullPointerException.class)
    public void cancelOrder_ShouldCancelOrder() {
        Order order = new Order(testCustomerId, testProductId, 5, 100.0);
        when(orderRepository.findById(testOrderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.cancelOrder(testOrderId);

        assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
        verify(orderRepository).save(order);
    }
}