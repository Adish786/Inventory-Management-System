package com.payment.service;

import java.util.List;

import com.payment.model.Payment;
import com.payment.repository.PaymentRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


@Service
public class PaymentServiceImpl implements PaymentService{
	
	    private final PaymentRepo repository;

	public PaymentServiceImpl(PaymentRepo repository) {
		this.repository = repository;
	}


	public Payment savePayment(Payment payment) {
	        return repository.save(payment);
	    }

	    public List<Payment> savePayments(List<Payment> payments) {
	        return repository.saveAll(payments);
	    }

	    public List<Payment> getPayments() {
	        return repository.findAll();
	    }

	    public Payment getPaymentById(int id) {
	        return repository.findById(id);
	    }

	    public Payment getPaymentServiceProvider(String name) {
	        return repository.findBypaymentserviceprovider(name);
	    }

	    public String deletePayment(int id) {
	        repository.deleteById(id);
	        return "Payment removed !! " + id;
	    }
	    

	    @Transactional
	    public Payment updatePayment(Payment payment) {
	    	Payment existingPayment = repository.findById(payment.getId());
	    	existingPayment.setId(payment.getId());
	    	existingPayment.setPaymentserviceprovider(payment.getPaymentserviceprovider());
	    	existingPayment.setQuantity(payment.getQuantity());
	    	existingPayment.setTotalpayout(payment.getTotalpayout());
	        return repository.save(existingPayment);
	    }

   

}
