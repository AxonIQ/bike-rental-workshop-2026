package io.axoniq.demo.bikerental.commands;

import org.axonframework.commandhandling.RoutingKey;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record RegisterBikeCommand(
        @TargetAggregateIdentifier
        String bikeId,
        String bikeType,
        String location
) {

}
