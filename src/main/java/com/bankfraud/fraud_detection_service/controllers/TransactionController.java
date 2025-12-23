package com.bankfraud.fraud_detection_service.controllers;

import com.bankfraud.fraud_detection_service.dtos.TransactionRequestDTO;
import com.bankfraud.fraud_detection_service.kafka.TransactionsProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionsProducer producer;

    public TransactionController(TransactionsProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<String> submitTransaction(
            @RequestBody TransactionRequestDTO dto) {

        producer.sendTransaction(dto);

        return ResponseEntity.ok("Transaction sent to Kafka successfully");
    }
}
