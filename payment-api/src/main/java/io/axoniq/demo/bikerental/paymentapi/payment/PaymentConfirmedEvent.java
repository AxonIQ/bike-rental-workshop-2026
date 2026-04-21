package io.axoniq.demo.bikerental.paymentapi.payment;

public record PaymentConfirmedEvent(String paymentId, String paymentReference) {

}