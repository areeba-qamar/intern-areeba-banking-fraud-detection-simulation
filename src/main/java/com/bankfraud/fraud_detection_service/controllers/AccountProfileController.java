package com.bankfraud.fraud_detection_service.controllers;

import com.bankfraud.fraud_detection_service.dtos.AccountProfileRequestDTO;
import com.bankfraud.fraud_detection_service.services.AccountProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account-profiles")
public class AccountProfileController {

    private final AccountProfileService service;

    public AccountProfileController(AccountProfileService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> createProfile(
            @RequestBody AccountProfileRequestDTO dto) {

        return ResponseEntity.ok(service.createProfile(dto));
    }
}
