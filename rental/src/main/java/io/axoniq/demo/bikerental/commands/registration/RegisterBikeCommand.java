package io.axoniq.demo.bikerental.commands.registration;

import org.axonframework.messaging.commandhandling.annotation.Command;
import org.axonframework.modelling.annotation.TargetEntityId;

@Command(namespace = "rental", name = "RegisterBikeCommand", routingKey = "bikeId")
public record RegisterBikeCommand(
        String bikeId,
        String bikeType,
        String location
) {

    @TargetEntityId
    public BikeLocation id() {
        return new BikeLocation(bikeId, location);
    }
}
