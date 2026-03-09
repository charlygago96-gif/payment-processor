package com.payment.application.usecase;

import com.payment.domain.exception.PaymentNotFoundException;
import com.payment.domain.model.Payment;
import com.payment.domain.port.in.GetPaymentUseCase;
import com.payment.domain.port.out.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetPaymentUseCaseImpl implements GetPaymentUseCase {

    private final PaymentRepository paymentRepository;

    public GetPaymentUseCaseImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment findById(String id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
    }

    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }
}