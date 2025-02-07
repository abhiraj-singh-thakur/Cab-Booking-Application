package org.example.uber.strategy;

import lombok.RequiredArgsConstructor;
import org.example.uber.entities.enums.PaymentMethod;
import org.example.uber.strategy.impl.CashPaymentStrategy;
import org.example.uber.strategy.impl.WalletPaymentStrategy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentStrategyManager {

    private final WalletPaymentStrategy walletPaymentStrategy;
    private final CashPaymentStrategy cashPaymentStrategy;

    public PaymentStrategy paymentStrategy(PaymentMethod paymentMethod){
        return switch (paymentMethod){
            case WALLET -> walletPaymentStrategy;
            case CASH -> cashPaymentStrategy;
            default -> throw new IllegalArgumentException("Invalid payment method");
        };
    }
}
