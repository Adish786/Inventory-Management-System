package com.inventory.service;

import com.inventory.model.Sales;
import com.inventory.model.SalesReport;
import com.inventory.repository.SalesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;

@Slf4j
@Service
public class SalesReportServiceImpl implements SalesReportService {
    private final SalesRepository salesRepository;
    public SalesReportServiceImpl(SalesRepository salesRepository) {
        this.salesRepository = salesRepository;
    }

    @Override
    @Cacheable("products")
    public SalesReport generateSalesReport(UUID productId) {
        List<Sales> salesList = salesRepository.findByProductId(productId);
        AtomicInteger totalSold = new AtomicInteger(0);
        DoubleAdder totalRevenue = new DoubleAdder();
        salesList.parallelStream().forEach(sale -> {
            totalSold.addAndGet(sale.getQuantitySold());
            totalRevenue.add(sale.getTotalSales());
        });

        return new SalesReport(productId, totalSold.get(), totalRevenue.doubleValue());
    }
}

