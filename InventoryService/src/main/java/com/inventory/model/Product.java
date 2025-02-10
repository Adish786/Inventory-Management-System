package com.inventory.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;


@Data
@Entity
@Table(name = "PRODUCT_TBL")
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;
    private String company;
    private int quantity;
    private double price;





    public Product() {
        super();
        // TODO Auto-generated constructor stub
    }

}
