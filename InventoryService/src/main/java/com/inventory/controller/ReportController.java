package com.inventory.controller;

import com.inventory.service.ReportGenerationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportGenerationService reportGenerationService;

    public ReportController(ReportGenerationService reportGenerationService) {
        this.reportGenerationService = reportGenerationService;
    }

    @GetMapping("/stock/{productId}")
    @ApiOperation(value = "get Inventory details", notes = "add Inventory details by productId")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public void getStockReport(@PathVariable UUID productId) {
        reportGenerationService.generateStockReport(productId);
    }

    @GetMapping("/sales/{productId}")
    @ApiOperation(value = "get Inventory details", notes = "get Inventory details by productId")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public void getSalesReport(@PathVariable UUID productId) {
        reportGenerationService.generateSalesReport(productId);
    }
}

