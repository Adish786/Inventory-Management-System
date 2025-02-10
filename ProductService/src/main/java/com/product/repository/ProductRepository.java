package com.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.product.model.Product;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
   Product findByName(String name);
}

