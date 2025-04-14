package com.service;

import com.service.model.Payment;
import com.service.repository.PaymentRepo;
import com.service.service.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {

    @Mock
    private PaymentRepo paymentRepo;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment testPayment;
    private List<Payment> testPayments;

    @BeforeEach
    void setUp() {
        testPayment = new Payment(1, "PayPal", 2, 100);
        testPayments = Arrays.asList(
                new Payment(1, "PayPal", 2, 100),
                new Payment(2, "Stripe", 3, 150)
        );
    }

    @Test
    void savePayment_ShouldReturnSavedPayment() {
        when(paymentRepo.save(any(Payment.class))).thenReturn(testPayment);

        Payment result = paymentService.savePayment(testPayment);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("PayPal", result.getPaymentserviceprovider());
        verify(paymentRepo).save(testPayment);
    }

    @Test
    void savePayments_ShouldReturnListOfSavedPayments() {
        when(paymentRepo.saveAll(anyList())).thenReturn(testPayments);

        List<Payment> result = paymentService.savePayments(testPayments);

        assertEquals(2, result.size());
        verify(paymentRepo).saveAll(testPayments);
    }

    @Test
    void getPayments_ShouldReturnAllPayments() {
        when(paymentRepo.findAll()).thenReturn(testPayments);

        List<Payment> result = paymentService.getPayments();

        assertEquals(2, result.size());
        verify(paymentRepo).findAll();
    }

    @Test
    void getPaymentById_ShouldReturnPayment_WhenExists() {
        when(paymentRepo.findById(1)).thenReturn(testPayment);

        Payment result = paymentService.getPaymentById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(paymentRepo).findById(1);
    }

    @Test
    void getPaymentById_ShouldReturnNull_WhenNotExists() {
        when(paymentRepo.findById(999)).thenReturn(null);

        Payment result = paymentService.getPaymentById(999);

        assertNull(result);
    }

    @Test
    void getPaymentServiceProvider_ShouldReturnPayment() {
        when(paymentRepo.findBypaymentserviceprovider("PayPal")).thenReturn(testPayment);

        Payment result = paymentService.getPaymentServiceProvider("PayPal");

        assertNotNull(result);
        assertEquals("PayPal", result.getPaymentserviceprovider());
        verify(paymentRepo).findBypaymentserviceprovider("PayPal");
    }

    @Test
    void deletePayment_ShouldReturnSuccessMessage() {
        String result = paymentService.deletePayment(1);

        assertEquals("Payment removed !! 1", result);
        verify(paymentRepo).deleteById(1);
    }

    @Test
    void updatePayment_ShouldUpdateExistingPayment() {
        Payment updatedPayment = new Payment(1, "Stripe", 3, 150);

        when(paymentRepo.findById(1)).thenReturn(testPayment);
        when(paymentRepo.save(any(Payment.class))).thenReturn(updatedPayment);

        Payment result = paymentService.updatePayment(updatedPayment);

        assertNotNull(result);
        assertEquals("Stripe", result.getPaymentserviceprovider());
        assertEquals(150, result.getQuantity());
        verify(paymentRepo).findById(1);
        verify(paymentRepo).save(testPayment);
    }

    @Test
    void updatePayment_ShouldThrowException_WhenPaymentNotFound() {
        Payment nonExistentPayment = new Payment(999, "Unknown", 0, 0);

        when(paymentRepo.findById(999)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            paymentService.updatePayment(nonExistentPayment);
        });
    }
}
