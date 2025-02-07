package org.example.uber.dto;

import lombok.Data;
import org.example.uber.entities.User;
import org.example.uber.entities.WalletTransaction;

import java.util.List;
@Data
public class WalletDTO {
    private Long id;

    private UserDTO user;

    private Double balance;

    private List<WalletTransactionDTO> transactions;
}
