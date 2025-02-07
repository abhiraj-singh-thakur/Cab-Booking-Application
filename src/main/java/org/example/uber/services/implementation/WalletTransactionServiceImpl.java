package org.example.uber.services.implementation;

import lombok.RequiredArgsConstructor;
import org.example.uber.entities.WalletTransaction;
import org.example.uber.repositories.WalletTransactionRepository;
import org.example.uber.services.WalletTransactionService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletTransactionServiceImpl implements WalletTransactionService {

    private final WalletTransactionRepository walletTransactionRepository;
    private final ModelMapper modelMapper;

    @Override
    public void createNewWalletTransaction(WalletTransaction walletTransaction) {
        walletTransactionRepository.save(walletTransaction);
    }
}
