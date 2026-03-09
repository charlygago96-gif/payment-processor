package com.payment.domain.port.in;

import com.payment.domain.model.Payment;
import com.payment.domain.model.PaymentType;
import java.math.BigDecimal;

public interface ProcessPaymentUseCase {
    Payment process(String sourceIban, String targetIban,
                    BigDecimal amount, String currency, PaymentType type);
}