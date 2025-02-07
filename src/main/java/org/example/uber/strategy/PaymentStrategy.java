package org.example.uber.strategy;

import org.example.uber.entities.Payment;

public interface PaymentStrategy {
    static final Double PLATFORM_FEE = 0.3;
    void processPayment(Payment payment);
}
