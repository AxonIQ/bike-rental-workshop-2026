package io.axoniq.demo.bikerental.paymentapi.payment;

import org.axonframework.serialization.Revision;

@Revision("0.0.1")
public record PaymentConfirmedEvent(String paymentId, String paymentReference) {

}