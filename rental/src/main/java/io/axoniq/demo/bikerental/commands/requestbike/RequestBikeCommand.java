package io.axoniq.demo.bikerental.commands.requestbike;

import org.axonframework.messaging.commandhandling.annotation.Command;
import org.axonframework.modelling.annotation.TargetEntityId;

@Command(namespace = "rental", name = "RequestBikeCommand", routingKey = "bikeId")
public record RequestBikeCommand(
        String bikeId,
        String renter,
        String reference) {
}
