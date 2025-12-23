package com.bankfraud.fraud_detection_service.business;

import lombok.Data;


import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Holds the result of fraud evaluation.
 * Pure business object:
 * - No DB
 * - No Kafka
 * - No Spring dependencies
 */

@Data
public class FraudDecision {

    /**
     * Final decision flag after scoring.
     */
    private boolean fraudulent;

    /**
     * All triggered fraud rules.
     */
    private Set<FraudRuleType> triggeredRules = new HashSet<>();

    /**
     * Composite fraud score (0–100).
     */
    private BigDecimal score = BigDecimal.ZERO;

    /* ---------------- Business helpers ---------------- */

    public void addRule(FraudRuleType rule) {
        triggeredRules.add(rule);
    }

    public boolean hasAlerts() {
        return !triggeredRules.isEmpty();
    }
}
