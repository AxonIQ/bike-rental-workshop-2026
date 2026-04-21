package io.axoniq.demo.bikerental.commands;

import org.axonframework.commandhandling.RoutingKey;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record RejectRequestCommand(
        @TargetAggregateIdentifier
        String bikeId,
        String renter) {

}
