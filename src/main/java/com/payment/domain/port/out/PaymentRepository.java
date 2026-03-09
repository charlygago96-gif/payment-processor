package com.payment.domain.port.out;

import com.payment.domain.model.Payment;
import java.util.Optional;
import java.util.List;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(String id);
    List<Payment> findAll();
}