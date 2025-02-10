package com.product.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table(name = "PRODUCT_TBL")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;
    private String company;
    private int quantity;
    @Embedded
    private Price price;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private boolean active;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Price getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Product [id=" + id + ", name=" + name + ", company=" + company + ", quantity=" + quantity + ", price="
                + price + "]";
    }

    public Product(UUID id, String name, String company, int quantity, Price price, Category category, boolean active) {
        this.id = id;
        this.name = name;
        this.company = company;
        this.quantity = quantity;
        this.price = price;
        this.category = category;
        this.active = active;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Product() {
        super();
        // TODO Auto-generated constructor stub
    }
    public Product(String name, String description, Price price, Category category) {
    }

    public void updatePrice(BigDecimal newPrice) {
        this.price = new Price(newPrice);
    }

    public void deactivate() {
        this.active = false;
    }

}
