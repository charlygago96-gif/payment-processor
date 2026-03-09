package com.payment.infrastructure.adapter.out.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class PaymentEntity {

    @Id
    private String id;
    private String sourceIban;
    private String targetIban;
    private BigDecimal amount;
    private String currency;
    private String type;
    private String status;
    private String processedBy;
    private LocalDateTime createdAt;

    public PaymentEntity() {}

    public PaymentEntity(String id, String sourceIban, String targetIban,
                         BigDecimal amount, String currency, String type,
                         String status, String processedBy, LocalDateTime createdAt) {
        this.id = id;
        this.sourceIban = sourceIban;
        this.targetIban = targetIban;
        this.amount = amount;
        this.currency = currency;
        this.type = type;
        this.status = status;
        this.processedBy = processedBy;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getSourceIban() { return sourceIban; }
    public String getTargetIban() { return targetIban; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public String getProcessedBy() { return processedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}