package com.inventory.controller;

import com.inventory.service.ReportGenerationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportControllerTest {

    @Mock
    private ReportGenerationService reportGenerationService;

    @InjectMocks
    private ReportController reportController;

    private UUID testProductId;

    @BeforeEach
    void setUp() {
        testProductId = UUID.randomUUID();
    }

    @Test
    void getStockReport_ShouldCallServiceMethod() {
        // Arrange
        doNothing().when(reportGenerationService).generateStockReport(testProductId);

        // Act
        reportController.getStockReport(testProductId);

        // Assert
        verify(reportGenerationService, times(1)).generateStockReport(testProductId);
    }

    @Test
    void getSalesReport_ShouldCallServiceMethod() {
        // Arrange
        doNothing().when(reportGenerationService).generateSalesReport(testProductId);

        // Act
        reportController.getSalesReport(testProductId);

        // Assert
        verify(reportGenerationService, times(1)).generateSalesReport(testProductId);
    }

    @Test
    void getStockReport_WhenServiceThrowsException_ShouldPropagate() {
        // Arrange
        doThrow(new RuntimeException("Report generation failed"))
                .when(reportGenerationService).generateStockReport(testProductId);

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> reportController.getStockReport(testProductId));
        verify(reportGenerationService, times(1)).generateStockReport(testProductId);
    }

    @Test
    void getSalesReport_WhenServiceThrowsException_ShouldPropagate() {
        // Arrange
        doThrow(new RuntimeException("Sales report generation failed"))
                .when(reportGenerationService).generateSalesReport(testProductId);

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> reportController.getSalesReport(testProductId));
        verify(reportGenerationService, times(1)).generateSalesReport(testProductId);
    }

    @Test
    void getStockReport_ShouldHandleNullProductId() {
        // Arrange
        doThrow(new IllegalArgumentException("Product ID cannot be null"))
                .when(reportGenerationService).generateStockReport(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> reportController.getStockReport(null));
        verify(reportGenerationService, times(1)).generateStockReport(null);
    }
}