package io.axoniq.demo.bikerental.paymentapi.payment;

public record PaymentRejectedEvent(String paymentId, String paymentReference) {

}

