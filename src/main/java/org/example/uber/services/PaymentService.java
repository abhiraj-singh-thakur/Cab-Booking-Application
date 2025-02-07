package org.example.uber.services;

import org.example.uber.entities.Payment;
import org.example.uber.entities.Ride;
import org.example.uber.entities.enums.PaymentStatus;

public interface PaymentService {

    void processPayment(Ride ride);

    Payment createNewPayment(Ride ride);

    void updatePaymentStatus(Payment payment, PaymentStatus status);
}
