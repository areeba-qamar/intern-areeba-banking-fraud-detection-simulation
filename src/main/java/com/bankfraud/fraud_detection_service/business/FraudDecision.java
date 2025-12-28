package com.bankfraud.fraud_detection_service.business;

import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


 // Holds the result of fraud evaluation : Pure business object:


@Data
public class FraudDecision {


     //Final decision flag after scoring.

    private boolean fraudulent;


     // All triggered fraud rules.

    private Set<FraudRuleType> triggeredRules = new HashSet<>();

    public List<FraudRuleType> getTriggeredRules() {
        return new ArrayList<>(triggeredRules);
    }

    private BigDecimal score = BigDecimal.ZERO;

    /* ---------------- Business helpers ---------------- */

    public void addRule(FraudRuleType rule) {
        triggeredRules.add(rule);
    }

    public boolean hasAlerts() {
        return fraudulent;
    }

}
