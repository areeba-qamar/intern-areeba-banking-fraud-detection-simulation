package com.bankfraud.fraud_detection_service.kafka;

import com.bankfraud.fraud_detection_service.dtos.TransactionRequestDTO;
import com.bankfraud.fraud_detection_service.entities.Transactions;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Sends transactions to Kafka for processing by TransactionsConsumer.
 */
@Component
public class TransactionsProducer {

    private static final String TOPIC = "transactions";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public TransactionsProducer(KafkaTemplate<String, String> kafkaTemplate,
                                ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendTransaction(TransactionRequestDTO dto) {
        try {
            String message = objectMapper.writeValueAsString(dto);
            kafkaTemplate.send(TOPIC, dto.getTransactionId(), message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send transaction to Kafka", e);
        }
    }
}
