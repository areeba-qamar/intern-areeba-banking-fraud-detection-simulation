package com.bankfraud.fraud_detection_service.business;


import java.math.BigDecimal;


public class FraudScoringService {

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

