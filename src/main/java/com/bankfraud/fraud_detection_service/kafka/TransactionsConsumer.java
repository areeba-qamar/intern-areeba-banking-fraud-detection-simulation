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
    private final ObjectMapper objectMapper;
    private final FraudDetectionFacade facade;
    private final StreamController streamController; // For SSE push


    // Constructor injection
    public TransactionsConsumer(FraudDetectionFacade facade,
                                ObjectMapper objectMapper,
                                StreamController streamController) {
        this.facade = facade;
        this.objectMapper = objectMapper;
        this.streamController = streamController;

    }


     // Kafka listener method: triggered when a message arrives on the 'transactions' topic.
     //@param record Kafka consumer record containing key, value, partition, offset

    @KafkaListener(topics = "transactions", groupId = "fraud-detection-group")
    public void consume(ConsumerRecord<String, String> record) {
        System.out.println(">>> Kafka listener HIT <<<");

        log.info("CONSUMER HIT !! Message received: {}", record.value());
        try {

            // Convert incoming JSON string to TransactionRequestDTO

            TransactionRequestDTO dto = objectMapper.readValue(record.value(), TransactionRequestDTO.class);

            //  Delegate business logic to service layer:
            // - DTO -> Entity mapping
            // - Persisting transaction to DB
            // - Fraud evaluation

            facade.process(dto);


            // Push live transaction to frontend via SSE

            try {
                streamController.pushTransaction(dto);

            } catch (Exception ex) {
                log.warn("Failed to push transaction SSE", ex);
            }

            System.out.println(">>> Transaction processed <<<");

            // Log success

            log.info("Transaction {} processed successfully.", dto.getTransactionId());

        } catch (Exception e) {
            // Log any errors in deserialization or processing
            log.error("Failed to process transaction message: {}", record.value(), e);
        }
    }
}
