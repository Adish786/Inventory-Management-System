package com.order.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID customerId;
    private UUID productId;
    private int quantity;
    private double totalPrice;
    private String status;  // PLACED, PAID, FULFILLED, CANCELLED
    private String orderType;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    public Order(UUID customerId, UUID productId, int quantity, double totalPrice) {
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.orderStatus = OrderStatus.PLACED;
    }

    public void updateOrder(int newQuantity, double newTotalPrice) {
        this.quantity = newQuantity;
        this.totalPrice = newTotalPrice;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void markAsPaid() {
        this.orderStatus = OrderStatus.PAID;
    }

    public void markAsFulfilled() {
        this.orderStatus = OrderStatus.FULFILLED;
    }

    public void cancelOrder() {
        this.orderStatus = OrderStatus.CANCELLED;
    }
}



