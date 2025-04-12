package com.inventory.service;

import com.inventory.model.Sales;
import com.inventory.model.SalesReport;
import com.inventory.repository.SalesRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
public class SalesReportServiceImpl implements SalesReportService{
    private final SalesRepository salesRepository;

    public SalesReportServiceImpl(SalesRepository salesRepository) {
        this.salesRepository = salesRepository;
    }

    @Cacheable("products")
    public SalesReport generateSalesReport(UUID productId) {
        List<Sales> salesList = salesRepository.findByProductId(productId);
        SalesReport report = new SalesReport(productId, 0, 0);

        for (Sales sale : salesList) {
            report.addSale(sale.getQuantitySold(), sale.getTotalSales());
        }
        return report;
    }
}
