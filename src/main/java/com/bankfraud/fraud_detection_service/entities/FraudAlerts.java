package com.bankfraud.fraud_detection_service.entities;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "fraud_alerts")
public class FraudAlerts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private String accountId;

    @Column(name = "alert_type")
    private String alertType;

    @Column(name = "alert_score")
    private BigDecimal alertScore;

    @Column(name = "related_txn_id")
    private String relatedTxnId;

    @Column(name = "details", columnDefinition = "jsonb")
    private String details; // store JSON as String

    @Column(name = "detected_at")
    private LocalDateTime detectedAt = LocalDateTime.now();

    @Column(name = "acknowledged")
    private Boolean acknowledged = false;

}

