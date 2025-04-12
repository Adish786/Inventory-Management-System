package com.service.service;

import java.util.List;

import com.service.model.Payment;
import com.service.repository.PaymentRepo;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepo repository;

    public PaymentServiceImpl(PaymentRepo repository) {
        this.repository = repository;
    }


    @Cacheable
    public Payment savePayment(Payment payment) {
        return repository.save(payment);
    }

    @Cacheable
    public List<Payment> savePayments(List<Payment> payments) {
        return repository.saveAll(payments);
    }

    @Cacheable
    public List<Payment> getPayments() {
        return repository.findAll();
    }

    @Cacheable
    public Payment getPaymentById(int id) {
        return repository.findById(id);
    }

    @Cacheable
    public Payment getPaymentServiceProvider(String name) {
        return repository.findBypaymentserviceprovider(name);
    }

    @Cacheable
    public String deletePayment(int id) {
        repository.deleteById(id);
        return "Payment removed !! " + id;
    }


    @Transactional
    @Cacheable("payments")
    public Payment updatePayment(Payment payment) {
        Payment existingPayment = repository.findById(payment.getId());
        existingPayment.setId(payment.getId());
        existingPayment.setPaymentserviceprovider(payment.getPaymentserviceprovider());
        existingPayment.setQuantity(payment.getQuantity());
        existingPayment.setTotalpayout(payment.getTotalpayout());
        return repository.save(existingPayment);
    }


}
