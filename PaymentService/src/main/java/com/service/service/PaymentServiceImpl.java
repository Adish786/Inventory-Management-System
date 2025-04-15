package com.service.service;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

import com.service.event.PaymentEvent;
import com.service.model.Payment;
import com.service.repository.PaymentRepo;
import jakarta.annotation.PreDestroy;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepo repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ApplicationEventPublisher eventPublisher;
    private final ConcurrentMap<Integer, ReentrantLock> paymentLocks = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    public PaymentServiceImpl(PaymentRepo repository,
                              KafkaTemplate<String, Object> kafkaTemplate,
                              ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
        this.eventPublisher = eventPublisher;
    }

    private ReentrantLock getLock(int paymentId) {
        return paymentLocks.computeIfAbsent(paymentId, id -> new ReentrantLock());
    }

    @Override
    @Cacheable("payments")
    public Payment savePayment(Payment payment) {
        log.info("Saving payment: {}", payment);
        Payment saved = repository.save(payment);
        publishEvents(saved);
        return saved;
    }

    @Override
    @Cacheable("payments")
    public List<Payment> savePayments(List<Payment> payments) {
        log.info("Saving list of payments, size: {}", payments.size());
        List<Payment> savedList = repository.saveAll(payments);
        savedList.forEach(this::publishEvents);
        return savedList;
    }

    @Override
    @Cacheable("payments")
    public List<Payment> getPayments() {
        log.debug("Fetching all payments");
        return repository.findAll();
    }

    @Override
    @Cacheable(value = "payments", key = "#id")
    public Payment getPaymentById(int id) {
        log.debug("Fetching payment by id: {}", id);
        return repository.findById(id);
    }

    @Override
    @Cacheable(value = "payments", key = "#name")
    public Payment getPaymentServiceProvider(String name) {
        log.debug("Fetching payment by service provider: {}", name);
        return repository.findBypaymentserviceprovider(name);
    }

    @Override
    @CacheEvict(value = "payments", key = "#id")
    public String deletePayment(int id) {
        ReentrantLock lock = getLock(id);
        lock.lock();
        try {
            log.warn("Deleting payment with id: {}", id);
            repository.deleteById(id);
            return "Payment removed !! " + id;
        } finally {
            lock.unlock();
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = "payments", key = "#payment.id")
    public Payment updatePayment(Payment payment) {
        int paymentId = payment.getId();
        ReentrantLock lock = getLock(paymentId);
        lock.lock();
        try {
            log.info("Updating payment with ID: {}", paymentId);
            Payment existing = repository.findById(paymentId);
            if (existing == null) {
                log.error("Payment not found for ID: {}", paymentId);
                throw new IllegalArgumentException("Payment not found");
            }

            existing.setPaymentserviceprovider(payment.getPaymentserviceprovider());
            existing.setQuantity(payment.getQuantity());
            existing.setTotalpayout(payment.getTotalpayout());

            Payment updated = repository.save(existing);
            publishEvents(updated);
            return updated;

        } finally {
            lock.unlock();
        }
    }

    private void publishEvents(Payment payment) {
        PaymentEvent event = new PaymentEvent(payment.getId(), payment.getPaymentserviceprovider(), payment.getTotalpayout());

        // Synchronous Spring Event
        eventPublisher.publishEvent(event);

        // Async Kafka event
        executorService.submit(() -> {
            try {
                kafkaTemplate.send("payment.topic", event);
                log.info("Published Kafka event for paymentId {}: {}", payment.getId(), event);
            } catch (Exception e) {
                log.error("Failed to send Kafka message for paymentId {}", payment.getId(), e);
            }
        });
    }

    @PreDestroy
    public void shutdownExecutor() {
        log.info("Shutting down executor service");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
