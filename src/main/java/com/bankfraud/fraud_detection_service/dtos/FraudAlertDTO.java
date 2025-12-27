package com.bankfraud.fraud_detection_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FraudAlertDTO {
    private Long id;
    private String accountId;
    private String alertType;
    private BigDecimal alertScore;
    private String relatedTxnId;
    private Map<String, Object> details;
    private LocalDateTime detectedAt;
    private Boolean acknowledged;
}

