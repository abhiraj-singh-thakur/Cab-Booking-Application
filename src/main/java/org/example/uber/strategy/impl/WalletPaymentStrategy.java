package org.example.uber.strategy.impl;

import lombok.RequiredArgsConstructor;
import org.example.uber.entities.Driver;
import org.example.uber.entities.Payment;
import org.example.uber.entities.Rider;
import org.example.uber.entities.enums.PaymentStatus;
import org.example.uber.entities.enums.TransactionMethod;   
import org.example.uber.repositories.PaymentRepository;
import org.example.uber.services.PaymentService;
import org.example.uber.services.RideService;
import org.example.uber.services.WalletService;
import org.example.uber.strategy.PaymentStrategy;
import org.springframework.stereotype.Service;


//Rider has 232, Driver 500
//Rider cost 100
//Rider = 232-100 = 132
//Driver = 500 + (100 -30) = 570

@Service
@RequiredArgsConstructor
public class WalletPaymentStrategy implements PaymentStrategy {

    private final WalletService walletService;
    private final PaymentRepository paymentRepository;

    @Override
    public void processPayment(Payment payment) {

        Driver driver = payment.getRide().getDriver();
        Rider rider = payment.getRide().getRider();

        walletService.detectMoneyFromWallet(rider.getUser(), payment.getAmount(), null, payment.getRide(), TransactionMethod.RIDE_TRANSACTION);

        double driversCut = payment.getAmount()*(1-PLATFORM_FEE);
        walletService.addMoneyToWallet(driver.getUser(), driversCut, null, payment.getRide(), TransactionMethod.RIDE_TRANSACTION);

        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(payment);


    }
}