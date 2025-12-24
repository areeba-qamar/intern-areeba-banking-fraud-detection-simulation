package com.bankfraud.fraud_detection_service.facade;


import org.springframework.stereotype.Service;

import com.bankfraud.fraud_detection_service.dtos.TransactionRequestDTO;
import com.bankfraud.fraud_detection_service.entities.Transactions;
import com.bankfraud.fraud_detection_service.services.FraudEvaluationService;
import com.bankfraud.fraud_detection_service.services.TransactionService;

@Service
public class FraudDetectionFacade {


    private final TransactionService transactionService;
    private final FraudEvaluationService fraudService;

    public FraudDetectionFacade(TransactionService transactionService,
                                FraudEvaluationService fraudService) {
        this.transactionService = transactionService;
        this.fraudService = fraudService;
    }

    public void process(TransactionRequestDTO dto) {

        // 1️⃣ Save transaction
        Transactions tx = transactionService.saveTransaction(dto);


        // 2️⃣ Gather context
        int recentCount =
                transactionService.countRecentTransactions(tx.getAccountId(), 2);

        boolean rapidTransfers =
                transactionService.hasRapidTransfers(tx.getAccountId());

        // 3️⃣ Evaluate fraud
        fraudService.evaluate(tx, recentCount, rapidTransfers);
    }
}

