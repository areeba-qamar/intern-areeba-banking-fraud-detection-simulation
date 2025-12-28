package com.bankfraud.fraud_detection_service.services;

import com.bankfraud.fraud_detection_service.dtos.AccountProfileRequestDTO;
import com.bankfraud.fraud_detection_service.entities.AccountProfiles;
import com.bankfraud.fraud_detection_service.repositories.AccountProfilesRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;


@Service
public class AccountProfileService {

    private final AccountProfilesRepository repo;

    public AccountProfileService(AccountProfilesRepository repo) {
        this.repo = repo;
    }

    public AccountProfiles createProfile(AccountProfileRequestDTO dto) {

        AccountProfiles profile = new AccountProfiles();
        profile.setAccountId(dto.getAccountId());
        profile.setAvgDailySpend(dto.getAvgDailySpend());
        profile.setAvgTxnAmount(dto.getAvgTxnAmount());
        profile.setHomeCountry(dto.getHomeCountry());
        profile.setRiskTier(dto.getRiskTier());
        profile.setUpdatedAt(LocalDateTime.now());

        return repo.save(profile);
    }
}
