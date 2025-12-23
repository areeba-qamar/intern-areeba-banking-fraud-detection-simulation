package com.bankfraud.fraud_detection_service.business;


import com.bankfraud.fraud_detection_service.entities.AccountProfiles;
import com.bankfraud.fraud_detection_service.entities.Transactions;

import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * Core fraud rules implementation.
 *
 * IMPORTANT DESIGN NOTE:
 * ----------------------
 * - This class intentionally uses NO dependency injection.
 * - It is NOT a Spring @Component or @Service.
 * - All required data is passed as method parameters.
 *
 * Reason:
 * - Keeps business logic pure
 * - Makes unit testing simple
 * - Avoids coupling with Spring / DB / Kafka
 */
public class FraudRuleEngine {

    /**
     * Absolute amount threshold.
     * (Later this can be moved to config and injected
     * via constructor if this class is promoted to @Component)
     */
    private static final BigDecimal ABSOLUTE_AMOUNT_THRESHOLD =
            new BigDecimal("100000");

    /**
     * Evaluates all fraud rules against a transaction.
     *
     * @param tx               incoming transaction
     * @param profile          account profile (avg spend, baseline)
     * @param recentTxCount    number of recent transactions (velocity)
     * @param geoMismatch      flag computed by service layer
     * @param rapidTransfers   flag computed by service layer
     * @return FraudDecision containing triggered rules
     */
    public FraudDecision evaluate(Transactions tx,
                                  AccountProfiles profile,
                                  int recentTxCount,
                                  boolean geoMismatch,
                                  boolean rapidTransfers) {

        FraudDecision decision = new FraudDecision();

        /* ---------------- Rule 1: Unusual Amount ---------------- */
        if (profile != null && tx.getAmount() != null) {
            BigDecimal avg = profile.getAvgTxnAmount();

            if ((avg != null &&
                    tx.getAmount().compareTo(avg.multiply(BigDecimal.valueOf(3))) > 0)
                    || tx.getAmount().compareTo(ABSOLUTE_AMOUNT_THRESHOLD) > 0) {

                decision.addRule(FraudRuleType.UNUSUAL_AMOUNT);
            }
        }

        /* ---------------- Rule 2: Velocity ---------------- */
        if (recentTxCount >= 5) {
            decision.addRule(FraudRuleType.VELOCITY);
        }

        /* ---------------- Rule 3: Geo Mismatch ---------------- */
        if (geoMismatch) {
            decision.addRule(FraudRuleType.GEO_MISMATCH);
        }

        /* ---------------- Rule 4: Night Transactions ---------------- */
        LocalTime time = tx.getTimestamp().toLocalTime();

        if (time.isAfter(LocalTime.MIDNIGHT)
                && time.isBefore(LocalTime.of(4, 0))
                && tx.getAmount().compareTo(new BigDecimal("50000")) > 0) {

            decision.addRule(FraudRuleType.NIGHT_TX);
        }

        /* ---------------- Rule 5: Rapid Transfers ---------------- */
        if (rapidTransfers) {
            decision.addRule(FraudRuleType.RAPID_TRANSFER);
        }

        return decision;
    }
}

