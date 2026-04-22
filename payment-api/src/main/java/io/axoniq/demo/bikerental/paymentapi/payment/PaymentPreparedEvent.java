package io.axoniq.demo.bikerental.paymentapi.payment;

import org.axonframework.serialization.Revision;

@Revision("0.0.1")
public record PaymentPreparedEvent(String paymentId, int amount, String paymentReference) {

}