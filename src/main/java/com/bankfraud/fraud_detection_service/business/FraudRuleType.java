package com.bankfraud.fraud_detection_service.business;


 //Enum representing all supported fraud rules.

public enum FraudRuleType {

    UNUSUAL_AMOUNT,
    VELOCITY,
    GEO_MISMATCH,
    NIGHT_TX,
    RAPID_TRANSFER
}
