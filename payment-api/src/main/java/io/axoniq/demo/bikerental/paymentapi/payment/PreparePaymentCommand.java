package io.axoniq.demo.bikerental.paymentapi.payment;

import org.axonframework.commandhandling.RoutingKey;

public record PreparePaymentCommand (int amount, @RoutingKey String paymentReference) {
}
