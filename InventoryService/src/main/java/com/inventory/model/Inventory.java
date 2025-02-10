package com.inventory.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private UUID productId;

    @Embedded
    private StockQuantity stockQuantity;
    private LocalDateTime lastUpdated;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public StockQuantity getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(StockQuantity stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Inventory(UUID productId, int quantity) {
        this.productId = productId;
        this.stockQuantity = new StockQuantity(quantity);
    }

    public void increaseStock(int quantity) {
        this.stockQuantity = stockQuantity.add(quantity);
    }

    public void decreaseStock(int quantity) {
        this.stockQuantity = stockQuantity.subtract(quantity);
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}

