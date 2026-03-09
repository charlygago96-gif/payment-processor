package com.payment.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Payment {

    private final String id;
    private final String sourceIban;
    private final String targetIban;
    private final Money amount;
    private final PaymentType type;
    private final LocalDateTime createdAt;
    private PaymentStatus status;
    private String processedBy;

    public Payment(String sourceIban, String targetIban, Money amount, PaymentType type) {
        this.id = UUID.randomUUID().toString();
        this.sourceIban = sourceIban;
        this.targetIban = targetIban;
        this.amount = amount;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        this.status = PaymentStatus.RECIBIDO;
    }

    public void startProcessing(String processor) {
        this.status = PaymentStatus.PROCESANDO;
        this.processedBy = processor;
    }

    public void complete() {
        this.status = PaymentStatus.COMPLETADO;
    }

    public void fail() {
        this.status = PaymentStatus.FALLIDO;
    }

    public String getId() { return id; }
    public String getSourceIban() { return sourceIban; }
    public String getTargetIban() { return targetIban; }
    public Money getAmount() { return amount; }
    public PaymentType getType() { return type; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public PaymentStatus getStatus() { return status; }
    public String getProcessedBy() { return processedBy; }
}