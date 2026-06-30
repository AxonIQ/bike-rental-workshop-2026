package io.axoniq.demo.bikerental.commands;

import org.axonframework.messaging.commandhandling.annotation.Command;
import org.axonframework.modelling.annotation.TargetEntityId;

@Command(namespace = "maintenance", name = "MarkBikeDamaged", routingKey = "bikeId")
public record MarkBikeDamaged(
        @TargetEntityId
        String bikeId
) {

}
