package com.bankfraud.fraud_detection_service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
@EnableJpaRepositories(basePackages = "com.bankfraud.fraud_detection_service.repositories")
@EntityScan(basePackages = "com.bankfraud.fraud_detection_service.entities")
public class FraudDetectionServiceApplication {

    private static final Logger log =
            LoggerFactory.getLogger(FraudDetectionServiceApplication.class);

    public static void main(String[] args) {
        log.info("Fraud Detection Service Backend Started Successfully");
        SpringApplication.run(FraudDetectionServiceApplication.class, args);
    }
}
