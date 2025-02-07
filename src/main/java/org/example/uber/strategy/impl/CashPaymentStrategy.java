package org.example.uber.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.example.uber.entities.Driver;
import org.example.uber.entities.Payment;
import org.example.uber.entities.enums.PaymentStatus;
import org.example.uber.entities.enums.TransactionMethod;
import org.example.uber.repositories.PaymentRepository;
import org.example.uber.services.PaymentService;
import org.example.uber.services.WalletService;
import org.example.uber.strategy.PaymentStrategy;
import org.springframework.stereotype.Service;

//Rider ->100
//Driver -> 70
@Service
@RequiredArgsConstructor
public class CashPaymentStrategy implements PaymentStrategy {

    private final WalletService walletService;
    private final PaymentRepository paymentRepository;

    @Override
    public void processPayment(Payment payment) {
        Driver driver = payment.getRide().getDriver();

        double platformCommission = payment.getAmount() * PLATFORM_FEE;

        walletService.detectMoneyFromWallet(driver.getUser(), platformCommission, null,
                payment.getRide(), TransactionMethod.BANK_TRANSACTION);

        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(payment);
    }
}

