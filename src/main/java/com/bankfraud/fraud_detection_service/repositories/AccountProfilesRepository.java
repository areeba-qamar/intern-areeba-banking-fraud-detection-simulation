package com.bankfraud.fraud_detection_service.repositories;

import com.bankfraud.fraud_detection_service.entities.AccountProfiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountProfilesRepository extends JpaRepository<AccountProfiles, String> {
    // default findById(String accountId) is already available
}
