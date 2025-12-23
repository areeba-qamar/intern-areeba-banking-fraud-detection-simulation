package com.bankfraud.fraud_detection_service.repositories;

import com.bankfraud.fraud_detection_service.entities.FraudAlerts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FraudAlertsRepository extends JpaRepository<FraudAlerts, Long> {
    // custom queries can be added later if needed
}
