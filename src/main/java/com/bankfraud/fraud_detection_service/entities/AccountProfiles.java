package com.bankfraud.fraud_detection_service.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account_profiles")
public class AccountProfiles {

    @Id
    @Column(name = "account_id")
    private String accountId;

    @Column(name = "avg_daily_spend")
    private BigDecimal avgDailySpend;

    @Column(name = "avg_txn_amount")
    private BigDecimal avgTxnAmount;

    @Column(name = "home_country")
    private String homeCountry;

    @Column(name = "risk_tier")
    private String riskTier; // LOW / MEDIUM / HIGH

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();




    // Getters, Setters, toString
}
