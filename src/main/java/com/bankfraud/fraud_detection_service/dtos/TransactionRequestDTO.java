package com.bankfraud.fraud_detection_service.dtos;

import java.math.BigDecimal;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestDTO {
    private String transactionId;
    private String accountId;
    private String txnType;
    private BigDecimal amount; // temporary double to avoid deserialization errors
    private String currency;
    private String location;
    private String merchant;
    private String timestamp; // parse later to LocalDateTime
}
