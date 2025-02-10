package com.inventory.repository;


import com.inventory.model.Sales;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SalesRepository extends JpaRepository<Sales, UUID> {
    List<Sales> findByProductId(UUID productId);
}

