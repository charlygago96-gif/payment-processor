package com.payment.domain.model;

// Tipos de pago — determinan la ruta que seguirá el mensaje en Camel
public enum PaymentType {
    NACIONAL,
    INTERNACIONAL,
    URGENTE
}