package io.axoniq.demo.bikerental.commands;

import org.axonframework.messaging.commandhandling.annotation.Command;
import org.axonframework.modelling.annotation.TargetEntityId;

@Command(namespace = "rental", name = "RegisterBikeCommand", routingKey = "bikeId")
public record RegisterBikeCommand(
        @TargetEntityId
        String bikeId,
        String bikeType,
        String location
) {

}
