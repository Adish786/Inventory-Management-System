package com.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import com.service.controller.OrderController;
import com.service.model.Order;
import com.service.repository.OrderRepository;
import com.service.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepo;

    @InjectMocks
    private OrderController orderController;
    @Mock
    private  UUID orderId;
    @Mock
    private UUID customerId;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    private Order createTestOrder(UUID id, UUID customerId, int quantity, double price) {
        return new Order(id, customerId, quantity, price);
    }

    @Test
    void addOrder_ShouldReturnCreatedOrder() throws Exception {
        orderId = UUID.randomUUID();
         customerId = UUID.randomUUID();
        Order order = createTestOrder(orderId, customerId, 5, 100.0);
        when(orderService.saveOrder(any())).thenReturn(order);
        mockMvc.perform(post("/addOrder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk());
        verify(orderService).saveOrder(any());
    }

    @Test
    void addOrders_ShouldReturnListOfOrders() throws Exception {
        orderId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        List<Order> orders = Arrays.asList(
                createTestOrder(orderId, customerId, 2, 50.0),
                createTestOrder(orderId, customerId, 3, 75.0)
        );

        when(orderService.saveOrders(anyList())).thenReturn(orders);

        mockMvc.perform(post("/addOrders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orders)))
                .andExpect(status().isOk());
        verify(orderService).saveOrders(anyList());
    }

    @Test
    void findAllOrders_ShouldReturnAllOrders() throws Exception {
        UUID customerId = UUID.randomUUID();
        List<Order> orders = Arrays.asList(
                createTestOrder(UUID.randomUUID(), customerId, 2, 50.0),
                createTestOrder(UUID.randomUUID(), customerId, 3, 75.0)
        );

        when(orderService.getOrders()).thenReturn(orders);

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(orderService).getOrders();
    }

    @Test
    void findOrderById_ShouldReturnOrder() throws Exception {
         orderId = UUID.randomUUID();
         customerId = UUID.randomUUID();
        Order order = createTestOrder(orderId, customerId, 5, 100.0);

        when(orderService.getOrderById(orderId)).thenReturn(order);

        mockMvc.perform(get("/orderById/{id}", orderId))
                .andExpect(status().isOk());
        verify(orderService).getOrderById(orderId);
    }

    @Test
    void deleteOrder_ShouldReturnSuccessMessage() throws Exception {
        UUID orderId = UUID.randomUUID();
        String successMessage = "Order removed " + orderId;

        when(orderService.deleteOrder(orderId)).thenReturn(successMessage);

        mockMvc.perform(delete("/delete/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(content().string(successMessage));

        verify(orderService).deleteOrder(orderId);
    }

    @Test
    void updateOrder_ShouldReturnUpdatedOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Order existingOrder = createTestOrder(orderId, customerId, 2, 50.0);
        Order updatedOrder = createTestOrder(orderId, customerId, 5, 100.0);

        when(orderRepo.findById(orderId)).thenReturn(Optional.of(existingOrder));
        when(orderRepo.save(any())).thenReturn(updatedOrder);

        mockMvc.perform(put("/update/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity", is(5)));

        verify(orderRepo).findById(orderId);
        verify(orderRepo).save(any());
    }

    @Test
    void updateOrder_ShouldReturnNotFound_WhenOrderDoesNotExist() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Order updatedOrder = createTestOrder(orderId, customerId, 5, 100.0);

        when(orderRepo.findById(orderId)).thenReturn(Optional.empty());

        mockMvc.perform(put("/update/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedOrder)))
                .andExpect(status().isNotFound());

        verify(orderRepo).findById(orderId);
        verify(orderRepo, never()).save(any());
    }
}