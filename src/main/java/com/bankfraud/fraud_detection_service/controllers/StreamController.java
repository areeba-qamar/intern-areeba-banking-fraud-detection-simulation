package com.bankfraud.fraud_detection_service.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.apache.kafka.common.requests.DeleteAclsResponse.log;

@RestController
@RequestMapping("/stream")
public class StreamController {

    private final List<SseEmitter> txEmitters = new CopyOnWriteArrayList<>();
    private final List<SseEmitter> alertEmitters = new CopyOnWriteArrayList<>();

    @GetMapping("/transactions")
    public SseEmitter streamTransactions() {
        log.info("SSE client connected for TRANSACTIONS");

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        txEmitters.add(emitter);

        emitter.onCompletion(() -> txEmitters.remove(emitter));
        emitter.onTimeout(() -> txEmitters.remove(emitter));
        emitter.onError(e -> txEmitters.remove(emitter));

        return emitter;
    }

    @GetMapping("/fraud-alerts")
    public SseEmitter streamAlerts() {

        log.info("SSE client connected for FRAUD_ALERTS");

        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        alertEmitters.add(emitter);

        emitter.onCompletion(() -> alertEmitters.remove(emitter));
        emitter.onTimeout(() -> alertEmitters.remove(emitter));
        emitter.onError(e -> alertEmitters.remove(emitter));

        log.info("SSE client connected");
        return emitter;
    }

    public void pushTransaction(Object data) {
        log.info("Pushing transaction to {} SSE clients", txEmitters.size());
        for (SseEmitter emitter : txEmitters) {
            try {
                emitter.send(SseEmitter.event().name("transaction").data(data));
            } catch (Exception e) {
                txEmitters.remove(emitter);
            }
        }
    }

    public void pushAlert(Object data) {

        log.info("Pushing alert to {} SSE clients", alertEmitters.size());

        for (SseEmitter emitter : alertEmitters) {
            try {
                emitter.send(SseEmitter.event().name("fraud-alert").data(data));
            } catch (Exception e) {
                alertEmitters.remove(emitter);
            }
        }
    }
}
