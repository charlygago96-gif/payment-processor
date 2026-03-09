package com.payment.domain.exception;

public class InvalidPaymentException extends RuntimeException {
    public InvalidPaymentException(String message) {
        super("Pago inválido: " + message);
    }
}