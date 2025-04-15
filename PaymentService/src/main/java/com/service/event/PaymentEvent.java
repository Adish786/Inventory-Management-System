package com.service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEvent {
    private int paymentId;
    private String provider;
    private BigDecimal total;

    // Constructors, getters, setters
}

