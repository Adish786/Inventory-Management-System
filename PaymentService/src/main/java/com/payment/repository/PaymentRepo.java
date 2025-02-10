package com.payment.repository;

import com.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepo extends JpaRepository<Payment,Integer> {
    Payment findBypaymentserviceprovider(String paymentserviceprovider);

//	Optional<Payment> findById(int id); 
    
    Payment findById(int id); 

}
