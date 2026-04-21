package io.axoniq.demo.bikerental.paymentapi.payment;

public record PaymentPreparedEvent(String paymentId, int amount, String paymentReference) {

}