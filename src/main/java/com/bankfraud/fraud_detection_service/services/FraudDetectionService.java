package com.bankfraud.fraud_detection_service.services;

import com.bankfraud.fraud_detection_service.business.*;
import com.bankfraud.fraud_detection_service.entities.FraudAlerts;
import com.bankfraud.fraud_detection_service.entities.Transactions;
import com.bankfraud.fraud_detection_service.entities.AccountProfiles;
import com.bankfraud.fraud_detection_service.repositories.FraudAlertsRepository;
import com.bankfraud.fraud_detection_service.repositories.AccountProfilesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.Local;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * Orchestrates fraud detection flow.
 *
 * Responsibilities:
 * 1. Fetch account profile and recent transaction info.
 * 2. Evaluate fraud rules using FraudRuleEngine.
 * 3. Calculate composite score.
 * 4. Persist FraudAlerts if fraud detected.
 * 5. Optionally publish alerts to Kafka.
 */
@Service
public class FraudDetectionService {

    private static final Logger log =
            LoggerFactory.getLogger(FraudDetectionService.class);

    private final TransactionService transactionService;
    private final AccountProfilesRepository profileRepo;
    private final FraudAlertsRepository alertRepo;
    private final KafkaTemplate<String, FraudAlerts> kafkaTemplate;

    // Core business logic objects (pure Java, no Spring)
    private final FraudRuleEngine ruleEngine = new FraudRuleEngine();
    private final FraudScoringService scoringService = new FraudScoringService();

    // Kafka topic for fraud alerts
    private static final String FRAUD_ALERTS_TOPIC = "fraud-alerts";

    public FraudDetectionService(TransactionService transactionService,
                                 AccountProfilesRepository profileRepo,
                                 FraudAlertsRepository alertRepo,
                                 KafkaTemplate<String, FraudAlerts> kafkaTemplate) {
        this.transactionService = transactionService;
        this.profileRepo = profileRepo;
        this.alertRepo = alertRepo;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Main evaluation method called from Kafka consumer.
     *
     * @param tx incoming transaction
     */
    public void evaluate(Transactions tx) {

        log.info("Starting fraud evaluation for transaction {}", tx.getTransactionId());

        // 1. Fetch account profile
        AccountProfiles profile = profileRepo.findById(tx.getAccountId()).orElse(null);

        // 2. Get recent transaction count for velocity rule
        int recentCount = transactionService.countRecentTransactions(tx.getAccountId(), 2);

        // 3. TODO: Compute geo-mismatch and rapid-transfers flags if needed
        boolean geoMismatch = false;
        boolean rapidTransfers = transactionService.hasRapidTransfers(tx.getAccountId());

        // 4. Evaluate all fraud rules
        FraudDecision decision = ruleEngine.evaluate(tx, profile, recentCount, geoMismatch, rapidTransfers);

        // 5. Calculate composite fraud score
        decision.setScore(scoringService.calculateScore(decision));

        log.debug("Transaction {} evaluated. Triggered rules: {}, Score: {}",
                tx.getTransactionId(),
                decision.getTriggeredRules(),
                decision.getScore());

        // 6. Persist alert if any rule triggered and score >= threshold
        if (decision.hasAlerts() && decision.getScore().intValue() >= 50) {

            log.warn("Fraud detected for account {} | Transaction {} | Score {}",
                    tx.getAccountId(), tx.getTransactionId(), decision.getScore());

            // Create FraudAlerts entity
            FraudAlerts alert = new FraudAlerts();
            alert.setAccountId(tx.getAccountId());
            alert.setAlertType(decision.getTriggeredRules().toString()); // store triggered rules as string
            alert.setAlertScore(decision.getScore());
            alert.setRelatedTxnId(tx.getTransactionId());
            alert.setDetectedAt(LocalDateTime.now());
            alert.setAcknowledged(false);

            // Persist to DB
            alertRepo.save(alert);
            log.info("Fraud alert persisted for transaction {}", tx.getTransactionId());

            // Publish to Kafka topic for real-time UI consumption
            try {
                kafkaTemplate.send(FRAUD_ALERTS_TOPIC, alert);
                log.info("Fraud alert published to Kafka topic {}", FRAUD_ALERTS_TOPIC);
            } catch (Exception e) {
                log.error("Failed to publish fraud alert to Kafka for transaction {}",
                        tx.getTransactionId(), e);
            }
        }
    }
}
