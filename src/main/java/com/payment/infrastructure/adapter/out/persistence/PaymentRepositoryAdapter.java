package com.payment.infrastructure.adapter.out.persistence;

import com.payment.domain.model.Money;
import com.payment.domain.model.Payment;
import com.payment.domain.model.PaymentStatus;
import com.payment.domain.model.PaymentType;
import com.payment.domain.port.out.PaymentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;

    public PaymentRepositoryAdapter(PaymentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = new PaymentEntity(
                payment.getId(),
                payment.getSourceIban(),
                payment.getTargetIban(),
                payment.getAmount().amount(),
                payment.getAmount().currency(),
                payment.getType().name(),
                payment.getStatus().name(),
                payment.getProcessedBy(),
                payment.getCreatedAt()
        );
        jpaRepository.save(entity);
        return payment;
    }

    @Override
    public Optional<Payment> findById(String id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Payment> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    private Payment toDomain(PaymentEntity e) {
        Money money = new Money(e.getAmount(), e.getCurrency());
        Payment payment = new Payment(
                e.getSourceIban(),
                e.getTargetIban(),
                money,
                PaymentType.valueOf(e.getType())
        );
        return payment;
    }
}