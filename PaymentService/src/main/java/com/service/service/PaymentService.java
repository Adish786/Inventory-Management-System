package com.service.service;

import com.service.model.Payment;

import java.util.List;

public interface PaymentService {
    Payment savePayment(Payment payment);
    List<Payment> savePayments(List<Payment> payments);
    List<Payment> getPayments();
    Payment getPaymentById(int id);
    Payment getPaymentServiceProvider(String name);
    String deletePayment(int id);
    Payment updatePayment(Payment payment);
}
