package com.payment.domain.port.in;

import com.payment.domain.model.Payment;
import java.util.List;

public interface GetPaymentUseCase {
    Payment findById(String id);
    List<Payment> findAll();
}