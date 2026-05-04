package io.axoniq.demo.bikerental.commands;

import org.axonframework.messaging.commandhandling.annotation.Command;
import org.axonframework.modelling.annotation.TargetEntityId;

@Command(namespace = "rental", name = "RejectRequestCommand", routingKey = "bikeId")
public record RejectRequestCommand(
        @TargetEntityId
        String bikeId,
        String renter) {

}
