package org.example.uber.dto;

import lombok.Builder;
import lombok.Value;
import org.example.uber.entities.Ride;
import org.example.uber.entities.enums.TransactionMethod;
import org.example.uber.entities.enums.TransactionType;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
public class WalletTransactionDTO {

   private Long id;

    private Double amount;;

    private TransactionType type;

    private TransactionMethod transactionMethod;

    private Ride ride;

    private String transactionId;

    private WalletDTO walletDTO;

    @CreationTimestamp
    private LocalDateTime timeStamp;

}