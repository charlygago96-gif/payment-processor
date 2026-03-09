package com.payment.application.usecase;

import com.payment.domain.model.Money;
import com.payment.domain.model.Payment;
import com.payment.domain.model.PaymentType;
import com.payment.domain.port.in.ProcessPaymentUseCase;
import com.payment.domain.port.out.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ProcessPaymentUseCaseImpl implements ProcessPaymentUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessPaymentUseCaseImpl.class);

    private final PaymentRepository paymentRepository;

    public ProcessPaymentUseCaseImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment process(String sourceIban, String targetIban,
                           BigDecimal amount, String currency, PaymentType type) {

        log.info("Procesando pago {} de {} a {}", type, sourceIban, targetIban);

        Money money = new Money(amount, currency);
        Payment payment = new Payment(sourceIban, targetIban, money, type);

        // Asigna el procesador según el tipo de pago
        String processor = switch (type) {
            case NACIONAL -> "SEPA-PROCESSOR";
            case INTERNACIONAL -> "SWIFT-PROCESSOR";
            case URGENTE -> "EXPRESS-PROCESSOR";
        };

        payment.startProcessing(processor);
        paymentRepository.save(payment);

        log.info("Pago {} asignado a {}", payment.getId(), processor);
        return payment;
    }
}