package io.axoniq.demo.bikerental.commands;

import org.axonframework.commandhandling.RoutingKey;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record ApproveRequestCommand(
        @RoutingKey
        @TargetAggregateIdentifier
        String bikeId,
        String renter) {

}
