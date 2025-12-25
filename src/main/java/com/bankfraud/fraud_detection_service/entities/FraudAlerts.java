package com.bankfraud.fraud_detection_service.entities;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

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
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> details;

    @Column(name = "detected_at")
    private LocalDateTime detectedAt = LocalDateTime.now();

    @Column(name = "acknowledged")
    private Boolean acknowledged = false;

}

