package com.inventory.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
@Data
@NoArgsConstructor
public class SalesReport {
    private UUID productId;
    private int totalSold;
    private double totalRevenue;

    public SalesReport(UUID productId, int totalSold, double totalRevenue) {
        this.productId = productId;
        this.totalSold = totalSold;
        this.totalRevenue = totalRevenue;
    }

    public void addSale(int quantitySold, double totalSaleAmount) {
        this.totalSold += quantitySold;
        this.totalRevenue += totalSaleAmount;
    }

    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public int getTotalSold() {
        return totalSold;
    }

    public void setTotalSold(int totalSold) {
        this.totalSold = totalSold;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}

