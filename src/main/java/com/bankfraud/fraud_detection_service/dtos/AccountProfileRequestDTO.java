package com.bankfraud.fraud_detection_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class AccountProfileRequestDTO {

    private String accountId;
    private BigDecimal avgDailySpend;
    private BigDecimal avgTxnAmount;
    private String homeCountry;
    private String riskTier; // LOW / MEDIUM / HIGH
}

