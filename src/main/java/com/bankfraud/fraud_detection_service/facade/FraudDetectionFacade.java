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

        // Save transaction

        Transactions tx = transactionService.saveTransaction(dto);


        // Evaluate fraud

        fraudService.evaluate(tx);
    }
}

