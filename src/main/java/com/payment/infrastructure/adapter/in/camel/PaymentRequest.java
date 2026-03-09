package com.payment.infrastructure.adapter.in.camel;

import com.payment.domain.model.PaymentType;
import java.math.BigDecimal;

public class PaymentRequest {

    private String sourceIban;
    private String targetIban;
    private BigDecimal amount;
    private String currency;
    private PaymentType type;

    public String getSourceIban() { return sourceIban; }
    public String getTargetIban() { return targetIban; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public PaymentType getType() { return type; }

    public void setSourceIban(String sourceIban) { this.sourceIban = sourceIban; }
    public void setTargetIban(String targetIban) { this.targetIban = targetIban; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setType(PaymentType type) { this.type = type; }
}