package io.axoniq.demo.bikerental.commands;

import org.axonframework.messaging.commandhandling.annotation.Command;
import org.axonframework.modelling.annotation.TargetEntityId;

@Command(namespace = "rental", name = "ReturnBikeCommand", routingKey = "bikeId")
public record ReturnBikeCommand(
        @TargetEntityId String bikeId,
        String location) {

}
