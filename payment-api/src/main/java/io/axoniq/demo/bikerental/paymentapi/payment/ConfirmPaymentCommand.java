package io.axoniq.demo.bikerental.paymentapi.payment;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record ConfirmPaymentCommand(@TargetAggregateIdentifier String paymentId) {

}