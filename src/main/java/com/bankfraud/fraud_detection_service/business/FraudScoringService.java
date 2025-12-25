package com.bankfraud.fraud_detection_service.business;


import java.math.BigDecimal;

/**
 * Assigns weights to triggered rules and computes the final fraud score.
 *
 * DESIGN:
 * - Pure business logic (no DB, Kafka, or Spring dependencies)
 * - Input: FraudDecision
 * - Output: Composite fraud score (0–100)
 */
public class FraudScoringService {

    /**
     * Calculates the total fraud score based on triggered rules.
     *
     * @param decision input FraudDecision
     * @return BigDecimal composite score
     *
     * Note for learning:
     * -----------------
     * The '->' in the switch is Java 14+ switch expression syntax (concise case handling).
     * It is NOT a lambda, but similar concepts are used in streams/lambdas.
     * Example: decision.getTriggeredRules().stream().map(...).reduce(...)
     */
    public BigDecimal calculateScore(FraudDecision decision) {

        BigDecimal score = BigDecimal.ZERO;

        for (FraudRuleType rule : decision.getTriggeredRules()) {
            // concise switch expression syntax
            switch (rule) {
                case UNUSUAL_AMOUNT -> score = score.add(BigDecimal.valueOf(50));
                case VELOCITY -> score = score.add(BigDecimal.valueOf(40));
                case GEO_MISMATCH -> score = score.add(BigDecimal.valueOf(45));
                case NIGHT_TX -> score = score.add(BigDecimal.valueOf(40));
                case RAPID_TRANSFER -> score = score.add(BigDecimal.valueOf(40));
            }
        }
        return score;
    }
}

