package com.service.service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import com.service.model.Payment;
import com.service.repository.PaymentRepo;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepo repository;

    // Locks map for per-payment locking to ensure thread safety
    private final ConcurrentMap<Integer, ReentrantLock> paymentLocks = new ConcurrentHashMap<>();

    public PaymentServiceImpl(PaymentRepo repository) {
        this.repository = repository;
    }

    private ReentrantLock getLock(int paymentId) {
        return paymentLocks.computeIfAbsent(paymentId, id -> new ReentrantLock());
    }

    @Override
    @Cacheable("payments")
    public Payment savePayment(Payment payment) {
        return repository.save(payment);
    }

    @Override
    @Cacheable("payments")
    public List<Payment> savePayments(List<Payment> payments) {
        return repository.saveAll(payments);
    }

    @Override
    @Cacheable("payments")
    public List<Payment> getPayments() {
        return repository.findAll();
    }

    @Override
    @Cacheable("payments")
    public Payment getPaymentById(int id) {
        return repository.findById(id);
    }

    @Override
    @Cacheable("payments")
    public Payment getPaymentServiceProvider(String name) {
        return repository.findBypaymentserviceprovider(name);
    }

    @Override
    public String deletePayment(int id) {
        ReentrantLock lock = getLock(id);
        lock.lock();
        try {
            repository.deleteById(id);
            return "Payment removed !! " + id;
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional
    public Payment updatePayment(Payment payment) {
        int paymentId = payment.getId();
        ReentrantLock lock = getLock(paymentId);
        lock.lock();
        try {
            Payment existingPayment = repository.findById(paymentId);
            if (existingPayment == null) {
                throw new IllegalArgumentException("Payment not found for ID: " + paymentId);
            }

            existingPayment.setPaymentserviceprovider(payment.getPaymentserviceprovider());
            existingPayment.setQuantity(payment.getQuantity());
            existingPayment.setTotalpayout(payment.getTotalpayout());

            return repository.save(existingPayment);
        } finally {
            lock.unlock();
        }
    }
}
