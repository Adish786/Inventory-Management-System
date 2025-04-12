package com.inventory.service;

import com.inventory.model.SalesReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportGenerationServiceImplTest {

    @Mock
    private SalesReportService salesReportService;

    @Mock
    private StockReportService stockReportService;

    @InjectMocks
    private ReportGenerationServiceImpl reportGenerationService;

    private UUID testProductId;

    @BeforeEach
    void setUp() {
        testProductId = UUID.randomUUID();
    }

    @Test
    void generateStockReport_ShouldCallStockReportService() {
        int expectedStockLevel = 50;
        when(stockReportService.getStockForProduct(testProductId)).thenReturn(expectedStockLevel);
        reportGenerationService.generateStockReport(testProductId);
        verify(stockReportService).getStockForProduct(testProductId);
    }

    @Test
    void generateStockReport_ShouldHandleZeroStock() {
        when(stockReportService.getStockForProduct(testProductId)).thenReturn(0);
        reportGenerationService.generateStockReport(testProductId);
        verify(stockReportService).getStockForProduct(testProductId);
    }

    @Test
    void generateSalesReport_ShouldCallSalesReportService() {
        SalesReport mockReport = new SalesReport(testProductId, 0,5000.0);
        when(salesReportService.generateSalesReport(testProductId)).thenReturn(mockReport);
        reportGenerationService.generateSalesReport(testProductId);
        verify(salesReportService).generateSalesReport(testProductId);
    }

    @Test
    void generateSalesReport_ShouldHandleEmptySales() {
        SalesReport mockReport = new SalesReport(testProductId,0, 0.0);
        when(salesReportService.generateSalesReport(testProductId)).thenReturn(mockReport);
        reportGenerationService.generateSalesReport(testProductId);
        verify(salesReportService).generateSalesReport(testProductId);
    }

    @Test
    void generateStockReport_ShouldHandleServiceException() {
        when(stockReportService.getStockForProduct(testProductId))
                .thenThrow(new RuntimeException("Service unavailable"));
        assertThrows(RuntimeException.class, () ->
                reportGenerationService.generateStockReport(testProductId));
    }
}