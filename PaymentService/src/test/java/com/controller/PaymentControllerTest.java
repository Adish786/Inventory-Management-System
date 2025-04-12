package com.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.Arrays;
import java.util.List;
import com.service.controller.PaymentController;
import com.service.model.Payment;
import com.service.repository.PaymentRepo;
import com.service.service.PaymentServiceImpl;
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

@ExtendWith(MockitoExtension.class)
public class PaymentControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PaymentServiceImpl paymentService;

    @Mock
    private PaymentRepo paymentRepo;

    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
    }

    @Test
    void addPayment_ShouldReturnCreatedPayment() throws Exception {
        Payment payment = new Payment(1, "PayPal", 2, 100);
        when(paymentService.savePayment(any(Payment.class))).thenReturn(payment);

        mockMvc.perform(post("/addPayment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.paymentserviceprovider").value("PayPal"));
    }

    @Test
    void addPayments_ShouldReturnListOfPayments() throws Exception {
        List<Payment> payments = Arrays.asList(
                new Payment(1, "PayPal", 2, 100),
                new Payment(2, "Stripe", 3, 150)
        );
        when(paymentService.savePayments(anyList())).thenReturn(payments);

        mockMvc.perform(post("/addPayments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payments)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void findAllPayments_ShouldReturnAllPayments() throws Exception {
        List<Payment> payments = Arrays.asList(
                new Payment(1, "PayPal", 2, 100),
                new Payment(2, "Stripe", 3, 150)
        );
        when(paymentService.getPayments()).thenReturn(payments);

        mockMvc.perform(get("/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void findPaymentById_ShouldReturnPayment() throws Exception {
        Payment payment = new Payment(1, "PayPal", 2, 100);
        when(paymentService.getPaymentById(1)).thenReturn(payment);

        mockMvc.perform(get("/paymentById/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.paymentserviceprovider").value("PayPal"));
    }

    @Test
    void findProductByPaymentServiceProvider_ShouldReturnPayment() throws Exception {
        Payment payment = new Payment(1, "PayPal", 2, 100);
        when(paymentService.getPaymentServiceProvider("PayPal")).thenReturn(payment);

        mockMvc.perform(get("/payment/{name}", "PayPal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentserviceprovider").value("PayPal"));
    }

    @Test
    void updatePayment_ShouldReturnUpdatedPayment() throws Exception {
        Payment payment = new Payment(1, "PayPal", 2, 100);
        when(paymentService.updatePayment(any(Payment.class))).thenReturn(payment);

        mockMvc.perform(put("/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deletePayment_ShouldReturnSuccessMessage() throws Exception {
        when(paymentService.deletePayment(1)).thenReturn("Payment deleted");

        mockMvc.perform(delete("/delete/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment deleted"));
    }

    @Test
    void updatePaymentById_ShouldReturnUpdatedPayment() throws Exception {
        Payment existingPayment = new Payment(1, "PayPal", 2, 100);
        Payment updatedPayment = new Payment(1, "Stripe", 3, 150);
        when(paymentRepo.findById(1)).thenReturn(existingPayment);
        when(paymentRepo.save(any(Payment.class))).thenReturn(updatedPayment);

        mockMvc.perform(put("/update/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPayment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentserviceprovider").value("Stripe"));
    }

    @Test
    void updatePaymentById_ShouldReturnNotFound_WhenPaymentDoesNotExist() throws Exception {
        Payment updatedPayment = new Payment(1, "Stripe", 3, 150);

        when(paymentRepo.findById(1)).thenReturn(updatedPayment);

        mockMvc.perform(put("/update/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPayment)))
                .andExpect(status().isOk());
    }
}
