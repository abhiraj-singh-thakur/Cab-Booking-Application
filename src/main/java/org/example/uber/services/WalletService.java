package org.example.uber.services;

import org.example.uber.entities.Ride;
import org.example.uber.entities.User;
import org.example.uber.entities.Wallet;
import org.example.uber.entities.enums.TransactionMethod;

public interface WalletService {

    Wallet addMoneyToWallet(User user, Double amount, String transactionId, Ride ride, TransactionMethod transactionMethod);

    void withdrawAllMyMoneyFromWallet(Wallet wallet);

    Wallet findWalletById(Long id);

    Wallet createNewWallet(User user);

    Wallet findByUser(User user);

    Wallet detectMoneyFromWallet(User user, Double amount, String transactionId, Ride ride, TransactionMethod transactionMethod);
}
