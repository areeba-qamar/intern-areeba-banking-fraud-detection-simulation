package com.bankfraud.fraud_detection_service.services;

import com.bankfraud.fraud_detection_service.business.*;
import com.bankfraud.fraud_detection_service.controllers.StreamController;
import com.bankfraud.fraud_detection_service.dtos.FraudAlertDTO;
import com.bankfraud.fraud_detection_service.entities.AccountProfiles;
import com.bankfraud.fraud_detection_service.entities.FraudAlerts;
import com.bankfraud.fraud_detection_service.entities.Transactions;
import com.bankfraud.fraud_detection_service.repositories.AccountProfilesRepository;
import com.bankfraud.fraud_detection_service.repositories.FraudAlertsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FraudEvaluationService {

    private static final Logger log =
            LoggerFactory.getLogger(FraudEvaluationService.class);

    private final AccountProfilesRepository profileRepo;
    private final FraudAlertsRepository alertRepo;
    private final KafkaTemplate<String, String> stringKafkaTemplate;
    private final ObjectMapper objectMapper;
    private final StreamController streamController; // SSE alerts
    private final TransactionService transactionService;

    //Pure business logic

    private final FraudRuleEngine ruleEngine = new FraudRuleEngine();
    private final FraudScoringService scoringService = new FraudScoringService();
    private static final String FRAUD_ALERTS_TOPIC = "fraud-alerts";

    public FraudEvaluationService(AccountProfilesRepository profileRepo,
                                  FraudAlertsRepository alertRepo,
                                  @Qualifier("stringKafkaTemplate")
                                  KafkaTemplate<String, String> stringKafkaTemplate,
                                  ObjectMapper objectMapper,
                                  StreamController streamController,
                                  TransactionService transactionService ) {


        this.profileRepo = profileRepo;
        this.alertRepo = alertRepo;
        this.stringKafkaTemplate = stringKafkaTemplate;
        this.objectMapper = objectMapper;
        this.streamController = streamController;
        this.transactionService = transactionService;
    }


     // Main Fraud Evaluation method

    public void evaluate(Transactions tx) {

        log.info(
                "Starting fraud evaluation | txId={} accountId={} amount={}",
                tx.getTransactionId(),
                tx.getAccountId(),
                tx.getAmount()
        );

        // Velocity: last 2 minutes
        LocalDateTime velocitySince =
                tx.getTimestamp().minusMinutes(2);

        // Rapid transfers: last 5 minutes
        LocalDateTime rapidSince =
                tx.getTimestamp().minusMinutes(5);

        int recentCount =
                transactionService.countTransactionsSince(
                        tx.getAccountId(),
                        velocitySince
                );

        boolean rapidTransfers =
                transactionService.countTransactionsSince(
                        tx.getAccountId(),
                        rapidSince
                ) >= 10;

        // Fetch account profile

        AccountProfiles profile =
                profileRepo.findById(tx.getAccountId()).orElse(null);

        // Geo Mismatch check

        boolean geoMismatch = false;

        if (profile != null
                && tx.getLocation() != null
                && profile.getHomeCountry() != null) {

            geoMismatch = !tx.getLocation()
                    .equalsIgnoreCase(profile.getHomeCountry());
        }

        // DEBUG CONTEXT LOG — VERY IMPORTANT

        log.info(
                "Fraud context | txId={} accountId={} recentTxCount={} rapidTransfers={} geoMismatch={}",
                tx.getTransactionId(),
                tx.getAccountId(),
                recentCount,
                rapidTransfers,
                geoMismatch
        );

        // Rule Evaluation

        FraudDecision decision = ruleEngine.evaluate(
                tx,
                profile,
                recentCount,
                geoMismatch,
                rapidTransfers
        );

        // Scoring : Calculate fraud Score

        decision.setScore(scoringService.calculateScore(decision));

        if (decision.getScore().compareTo(BigDecimal.valueOf(40)) >= 0) {
            decision.setFraudulent(true);
        } else {
            decision.setFraudulent(false);
        }

        log.debug(
                "Transaction {} evaluated | Rules: {} | Score: {}",
                tx.getTransactionId(),
                decision.getTriggeredRules(),
                decision.getScore()
        );

        // Persist & publish alert if fraud detected

        if (!decision.isFraudulent()) {
            return; // skip non-fraudulent transactions
        }

            log.warn(
                    "Fraud detected | Account {} | Tx {} | Score {}",
                    tx.getAccountId(),
                    tx.getTransactionId(),
                    decision.getScore()
            );


            // Build Alert Entity

            FraudAlerts alert = new FraudAlerts();
            alert.setAccountId(tx.getAccountId());
            alert.setAlertType(String.join(
                    ",",
                    decision.getTriggeredRules()
                            .stream()
                            .map(Enum::name)
                            .collect(Collectors.joining(","))
            ));
            alert.setAlertScore(decision.getScore());
            alert.setRelatedTxnId(tx.getTransactionId());
            alert.setDetectedAt(LocalDateTime.now());
            alert.setAcknowledged(false);

            /*  IMPORTANT PART (JSONB DETAILS) */

            Map<String, Object> details = new HashMap<>();
            details.put("triggeredRules", decision.getTriggeredRules());
            details.put("score", decision.getScore());
            details.put("transactionId", tx.getTransactionId());
            details.put("amount", tx.getAmount());
            details.put("location", tx.getLocation());
            details.put("timestamp", tx.getTimestamp());

            alert.setDetails(details);

         // Save Alert in Alert Table

           try {
               alertRepo.save(alert);
               log.info("Fraud alert persisted for transaction {}", tx.getTransactionId());
           }catch (Exception e) {
               log.error("Failed to persist fraud alert for tx {}", tx.getTransactionId(), e);
               return;
           }


            // Push fraud alert to frontend via SSE

        try {
            FraudAlertDTO dto = new FraudAlertDTO(
                    alert.getId(),
                    alert.getAccountId(),
                    alert.getAlertType(),
                    alert.getAlertScore(),
                    alert.getRelatedTxnId(),
                    alert.getDetails(),
                    alert.getDetectedAt(),
                    alert.getAcknowledged()
            );
            streamController.pushAlert(dto);
        } catch (Exception ex) {
                log.warn("Failed to push fraud alert SSE", ex);
        }

        // Publish to Kafka

            try {
                String alertJson = objectMapper.writeValueAsString(alert);
                stringKafkaTemplate.send(FRAUD_ALERTS_TOPIC, alertJson);

                log.info("Fraud alert published to Kafka topic {}", FRAUD_ALERTS_TOPIC);
            } catch (Exception e) {
                log.error(
                        "Failed to publish fraud alert for transaction {}",
                        tx.getTransactionId(),
                        e
                );
            }
        }
    }
