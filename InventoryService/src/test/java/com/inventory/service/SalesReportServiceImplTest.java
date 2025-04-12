package com.inventory.service;

import com.inventory.model.Sales;
import com.inventory.model.SalesReport;
import com.inventory.repository.SalesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SalesReportServiceImplTest {

    @Mock
    private SalesRepository salesRepository;

    @InjectMocks
    private SalesReportServiceImpl salesReportService;

    private UUID testProductId;
    private Sales testSale1;
    private Sales testSale2;

    @BeforeEach
    void setUp() {
        testProductId = UUID.randomUUID();
        testSale1 = new Sales(testProductId, 5, 250.0); // 5 items, $250 total
        testSale2 = new Sales(testProductId, 3, 180.0); // 3 items, $180 total
    }

    @Test
    void generateSalesReport_ShouldReturnEmptyReportForNoSales() {
        // Arrange
        when(salesRepository.findByProductId(testProductId))
                .thenReturn(Collections.emptyList());

        // Act
        SalesReport report = salesReportService.generateSalesReport(testProductId);

        // Assert
        assertEquals(testProductId, report.getProductId());
        assertEquals(0, report.getTotalSold());
        assertEquals(0.0, report.getTotalRevenue(), 0.001);
        verify(salesRepository).findByProductId(testProductId);
    }

    @Test
    void generateSalesReport_ShouldAggregateSingleSaleCorrectly() {
        // Arrange
        when(salesRepository.findByProductId(testProductId))
                .thenReturn(Collections.singletonList(testSale1));

        // Act
        SalesReport report = salesReportService.generateSalesReport(testProductId);

        // Assert
        assertEquals(testProductId, report.getProductId());
        assertEquals(5, report.getTotalSold());
        assertEquals(1250.0, report.getTotalRevenue(), 0.001);
    }

    @Test
    void generateSalesReport_ShouldAggregateMultipleSalesCorrectly() {
        // Arrange
        List<Sales> salesList = Arrays.asList(testSale1, testSale2);
        when(salesRepository.findByProductId(testProductId))
                .thenReturn(salesList);

        // Act
        SalesReport report = salesReportService.generateSalesReport(testProductId);

        // Assert
        assertEquals(testProductId, report.getProductId());
        assertEquals(8, report.getTotalSold()); // 5 + 3
        assertEquals(1790.0, report.getTotalRevenue(), 0.001); // 250 + 180
    }

    @Test
    void generateSalesReport_ShouldHandleZeroQuantitySales() {
        // Arrange
        Sales zeroSale = new Sales(testProductId, 0, 0.0);
        when(salesRepository.findByProductId(testProductId))
                .thenReturn(Collections.singletonList(zeroSale));

        // Act
        SalesReport report = salesReportService.generateSalesReport(testProductId);

        // Assert
        assertEquals(0, report.getTotalSold());
        assertEquals(0.0, report.getTotalRevenue(), 0.001);
    }

    @Test
    void generateSalesReport_ShouldHandleRepositoryException() {
        // Arrange
        when(salesRepository.findByProductId(testProductId))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                salesReportService.generateSalesReport(testProductId));
    }
}
