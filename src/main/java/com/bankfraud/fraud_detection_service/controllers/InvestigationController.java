package com.bankfraud.fraud_detection_service.controllers;

import com.bankfraud.fraud_detection_service.dtos.AccountProfileRequestDTO;
import com.bankfraud.fraud_detection_service.dtos.FraudAlertDTO;
import com.bankfraud.fraud_detection_service.dtos.TransactionRequestDTO;
import com.bankfraud.fraud_detection_service.repositories.AccountProfilesRepository;
import com.bankfraud.fraud_detection_service.repositories.FraudAlertsRepository;
import com.bankfraud.fraud_detection_service.repositories.TransactionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class InvestigationController {

    private final TransactionsRepository transactionsRepo;
    private final FraudAlertsRepository alertsRepo;
    private final AccountProfilesRepository profilesRepo;

    @GetMapping("/{accountId}/transactions")
    public List<TransactionRequestDTO> getRecentTransactions(@PathVariable String accountId,
                                                             @RequestParam(defaultValue = "50") int limit) {
        return transactionsRepo.findTop20ByAccountIdOrderByTimestampDesc(accountId)
                .stream()
                .map(tx -> new TransactionRequestDTO(
                        tx.getTransactionId(),
                        tx.getAccountId(),
                        tx.getTxnType(),
                        tx.getAmount(),
                        tx.getCurrency(),
                        tx.getLocation(),
                        tx.getMerchant(),
                        tx.getTimestamp().toString()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/{accountId}/alerts")
    public List<FraudAlertDTO> getRecentAlerts(@PathVariable String accountId,
                                               @RequestParam(defaultValue = "50") int limit) {
        return alertsRepo.findTop20ByAccountIdOrderByDetectedAtDesc(accountId)
                .stream()
                .map(alert -> new FraudAlertDTO(
                        alert.getId(),
                        alert.getAccountId(),
                        alert.getAlertType(),
                        alert.getAlertScore(),
                        alert.getRelatedTxnId(),
                        alert.getDetails(),
                        alert.getDetectedAt(),
                        alert.getAcknowledged()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/{accountId}/profile")
    public AccountProfileRequestDTO getAccountProfile(@PathVariable String accountId) {
        return profilesRepo.findById(accountId)
                .map(p -> new AccountProfileRequestDTO(
                        p.getAccountId(),
                        p.getAvgDailySpend(),
                        p.getAvgTxnAmount(),
                        p.getHomeCountry(),
                        p.getRiskTier()
                ))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}

