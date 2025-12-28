package com.bankfraud.fraud_detection_service.services;


import com.bankfraud.fraud_detection_service.dtos.TransactionRequestDTO;
import com.bankfraud.fraud_detection_service.entities.Transactions;
import com.bankfraud.fraud_detection_service.repositories.TransactionsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


 // Handles DB queries needed for fraud rules.

@Service
public class TransactionService {

    private final TransactionsRepository txRepo;

    public TransactionService(TransactionsRepository txRepo, FraudEvaluationService fraudService) {
        this.txRepo = txRepo;

    }

    public Transactions saveTransaction(TransactionRequestDTO dto) {
        Transactions tx = new Transactions();
        tx.setTransactionId(dto.getTransactionId());
        tx.setAccountId(dto.getAccountId());
        tx.setTxnType(dto.getTxnType());
        tx.setAmount(dto.getAmount());
        tx.setCurrency(dto.getCurrency());
        tx.setLocation(dto.getLocation());
        tx.setMerchant(dto.getMerchant());
        tx.setTimestamp(LocalDateTime.parse(dto.getTimestamp()));

        return txRepo.save(tx);
    }


    //Velocity check:
    //Count transactions in last N minutes.

    public int countRecentTransactions(String accountId, int minutes) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(minutes);
        return txRepo.countByAccountIdAndTimestampAfter(accountId, since);
    }


     //Rapid transfers window

    public boolean hasRapidTransfers(String accountId) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(5);
        int count = txRepo.countByAccountIdAndTimestampAfter(accountId, since);
        return count >= 3;
    }

}

