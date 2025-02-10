package com.order.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.order.model.Order;
import com.order.service.OrderService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.order.repository.OrderRepository;


@RestController
public class OrderController {
    @Autowired
    private OrderService service;
    @Autowired
    private OrderRepository orderRepo;

    @PostMapping("/addOrder")
    @ApiOperation(value = "add order details", notes = "add order details")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public Order addOrder(@RequestBody Order order) {
        return service.saveOrder(order);
    }

    @PostMapping("/addOrders")
    @ApiOperation(value = "add orders details", notes = "add orders details")
    @ApiResponses(value = { @ApiResponse(code = 201, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public List<Order> addOrders(@RequestBody List<Order> orders) {
        return service.saveOrders(orders);
    }

    @GetMapping("/orders")
    @ApiOperation(value = "get orders details", notes = "get orders details")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public List<Order> findAllOrders() {
        return service.getOrders();
    }

    @GetMapping("/orderById/{id}")
    @ApiOperation(value = "get order details", notes = "get order details by id")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public Order findOrderById(@PathVariable UUID id) {
        return service.getOrderById(id);
    }

    @GetMapping("/order/{name}")
    @ApiOperation(value = "get order details", notes = "get order details by name")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public Order findOrderType(@PathVariable String ordertype) {
        return service.getOrderType(ordertype);
    }



    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "delete order details", notes = "removed order details by id")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public String deleteOrder(@PathVariable UUID id) {
        return service.deleteOrder(id);
    }

    @PutMapping("update/{id}")
    @ApiOperation(value = "update order details", notes = "update order details by id")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 400, message = "Bad Request"),
            @ApiResponse(code = 500, message = "Internal Server Error") })
    public ResponseEntity<Order> update(@PathVariable("id") UUID id, @RequestBody Order order) {
        Optional<Order> optionalProject = orderRepo.findById(id);
        if (optionalProject.isPresent()) {
            Order p = optionalProject.get();
            if (order.getId() != null)
                p.setId(order.getId());
            if (order.getQuantity() != 0)
                p.setQuantity(order.getQuantity());
            if (order.getCustomerId() != null)
                p.setCustomerId(order.getCustomerId());
            return new ResponseEntity<>(orderRepo.save(p), HttpStatus.OK);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
