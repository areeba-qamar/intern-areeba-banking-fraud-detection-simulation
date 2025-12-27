package com.bankfraud.fraud_detection_service.kafka;

import com.bankfraud.fraud_detection_service.controllers.StreamController;
import com.bankfraud.fraud_detection_service.dtos.TransactionRequestDTO;
import com.bankfraud.fraud_detection_service.facade.FraudDetectionFacade;
import com.bankfraud.fraud_detection_service.services.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@Component
public class TransactionsConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransactionsConsumer.class);

    private final ObjectMapper objectMapper;             // Converts JSON to DTO
    private final FraudDetectionFacade facade;
    private final StreamController streamController; // 🔥 SSE push ke liye


    // Constructor injection
    public TransactionsConsumer(FraudDetectionFacade facade,
                                ObjectMapper objectMapper,
                                StreamController streamController) { // 🔥 inject{
        this.facade = facade;
        this.objectMapper = objectMapper;
        this.streamController = streamController;

    }

    /**
     * Kafka listener method: triggered when a message arrives on the 'transactions' topic.
     *
     * @param record Kafka consumer record containing key, value, partition, offset
     */

    @KafkaListener(topics = "transactions", groupId = "fraud-detection-group")
    public void consume(ConsumerRecord<String, String> record) {
        System.out.println(">>> Kafka listener HIT <<<");   // simple stdout
        log.info("CONSUMER HIT !! Message received: {}", record.value());
        try {
            // 1️⃣ Convert incoming JSON string to TransactionRequestDTO
            TransactionRequestDTO dto = objectMapper.readValue(record.value(), TransactionRequestDTO.class);

            // 2️⃣ Delegate business logic to service layer:
            // - DTO -> Entity mapping
            // - Persisting transaction to DB
            // - Fraud evaluation
            facade.process(dto);


            // 🔥 3️⃣ Push live transaction to frontend via SSE
            try {
                streamController.pushTransaction(dto);

            } catch (Exception ex) {
                log.warn("Failed to push transaction SSE", ex);
            }

            System.out.println(">>> Transaction processed <<<"); // stdout

            // 3️⃣ Log success
            log.info("Transaction {} processed successfully.", dto.getTransactionId());

        } catch (Exception e) {
            // Log any errors in deserialization or processing
            log.error("Failed to process transaction message: {}", record.value(), e);
            // Optional: send this message to a Dead Letter Queue (DLQ) or error tracking
        }
    }
}
