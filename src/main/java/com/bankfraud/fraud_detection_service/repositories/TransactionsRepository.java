package com.bankfraud.fraud_detection_service.repositories;

import com.bankfraud.fraud_detection_service.entities.Transactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionsRepository extends JpaRepository<Transactions, Long> {

    // Velocity rule: count recent transactions in last N minutes
    int countByAccountIdAndTimestampAfter(String accountId, LocalDateTime timestamp);
    List<Transactions> findTop20ByAccountIdOrderByTimestampDesc(String accountId);

}
