package com.inventory.model;


import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class StockQuantity {
    private int quantity;
    protected StockQuantity() {}
    public StockQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        this.quantity = quantity;
    }
    public StockQuantity add(int amount) {
        return new StockQuantity(this.quantity + amount);
    }

    public StockQuantity subtract(int amount) {
        if (this.quantity < amount) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        return new StockQuantity(this.quantity - amount);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
