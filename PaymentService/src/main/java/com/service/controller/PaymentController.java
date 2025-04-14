package com.service.controller;

import java.util.List;

import com.service.model.Payment;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.service.repository.PaymentRepo;
import com.service.service.PaymentServiceImpl;
import java.util.Optional;

@RestController
@Slf4j
public class PaymentController {

	private final PaymentServiceImpl service;
	private final PaymentRepo paymentRepo;

	public PaymentController(PaymentServiceImpl service, PaymentRepo paymentRepo) {
		this.service = service;
		this.paymentRepo = paymentRepo;
	}


	@RequestMapping(value = "/addPayment", method = RequestMethod.POST)
	@ApiOperation(value = "add payment details", notes = "add payment details")
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Success"),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	public Payment addPayment(@RequestBody Payment payment) {
		return service.savePayment(payment);
	}

	@RequestMapping(value = "/addPayments", method = RequestMethod.POST)
	@ApiOperation(value = "add payments details", notes = "add payments details")
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Success"),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	public List<Payment> addPayments(@RequestBody List<Payment> payments) {
		return service.savePayments(payments);
	}

	@RequestMapping(value = "/payments", method = RequestMethod.GET)
	@ApiOperation(value = "get payment details", notes = "get payment details")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	public List<Payment> findAllPayments() {
		return service.getPayments();
	}

	@RequestMapping(value = "/paymentById/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "get payments details", notes = "get payment details by id")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	public Payment findPaymentById(@PathVariable int id) {
		return service.getPaymentById(id);
	}

	@RequestMapping(value = "/payment/{name}", method = RequestMethod.GET)
	@ApiOperation(value = "get payment details", notes = "get payment details by name")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	public Payment findProductBypaymentserviceprovider(@PathVariable String name) {
		return service.getPaymentServiceProvider(name);
	}

	@RequestMapping(value = "/update", method = RequestMethod.PUT)
	@ApiOperation(value = "update payment details", notes = "update payment details")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	public Payment updatePayment(@RequestBody Payment payment) {
		return service.updatePayment(payment);
	}
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	@ApiOperation(value = "delete payment details", notes = "delete payment details by id")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 500, message = "Internal Server Error") })
	public String deletePayment(@PathVariable int id) {
		return service.deletePayment(id);
	}
	
	
	 @PutMapping("update/{id}")
	 @ApiOperation(value = "update payment details", notes = "update payment details by id")
	 @ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			 @ApiResponse(code = 400, message = "Bad Request"),
			 @ApiResponse(code = 500, message = "Internal Server Error") })
     public ResponseEntity<Payment> update(@PathVariable("id") int id, @RequestBody Payment payment) {
         java.util.Optional<Payment> optionalProject = Optional.of(paymentRepo.findById(id));
         if (optionalProject.isPresent()) {
        	 Payment p = optionalProject.get();
             if (payment.getPaymentserviceprovider() != null)
                 p.setPaymentserviceprovider(payment.getPaymentserviceprovider());
             if (payment.getQuantity() !=0)
                 p.setQuantity(payment.getQuantity());
             if (payment.getTotalpayout() != 0)
                 p.setTotalpayout(payment.getTotalpayout());
             return new ResponseEntity<>(paymentRepo.save(p), HttpStatus.OK);
         } else
             return new ResponseEntity<>(HttpStatus.NOT_FOUND);
     }
}
