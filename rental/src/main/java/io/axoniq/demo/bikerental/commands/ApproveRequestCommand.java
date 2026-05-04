package io.axoniq.demo.bikerental.commands;

import org.axonframework.messaging.commandhandling.annotation.Command;
import org.axonframework.modelling.annotation.TargetEntityId;

@Command(namespace = "rental", name = "ApproveRequestCommand", routingKey = "bikeId")
public record ApproveRequestCommand(
        @TargetEntityId
        String bikeId,
        String renter) {
}
