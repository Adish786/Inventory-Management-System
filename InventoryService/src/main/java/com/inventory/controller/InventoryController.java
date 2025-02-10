package com.inventory.controller;

import com.inventory.model.StockQuantity;
import com.inventory.service.InventoryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {


    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }


    @GetMapping("/{productId}")
    @ApiOperation(value = "get Inventory details", notes = "get Inventory details by productId")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public ResponseEntity<Integer> checkStock(@PathVariable UUID productId) {
        return ResponseEntity.ok(inventoryService.getStock(productId));
    }

    @PostMapping("/{productId}/{quantity}")
    @ApiOperation(value = "add Inventory details", notes = "add Inventory details by productId and quantity")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public ResponseEntity<String> updateStock(@PathVariable UUID productId, @PathVariable StockQuantity quantity) {
        inventoryService.updateStock(productId, quantity);
        return ResponseEntity.ok("Stock updated");
    }

    @PostMapping("/increase/{productId}/{quantity}")
    @ApiOperation(value = "add Inventory details", notes = "add Inventory details by productId and quantity and increase ")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public ResponseEntity<Void> increaseStock(@PathVariable UUID productId, @PathVariable int quantity) {
        inventoryService.increaseStock(productId, quantity);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/decrease/{productId}/{quantity}")
    @ApiOperation(value = "add Inventory details", notes = "add Inventory details by productId and quantity and decrease")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public ResponseEntity<Void> decreaseStock(@PathVariable UUID productId, @PathVariable int quantity) {
        inventoryService.decreaseStock(productId, quantity);
        return ResponseEntity.noContent().build();
    }
}

