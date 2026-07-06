package io.axoniq.demo.bikerental.domain;

import io.axoniq.demo.bikerental.events.BikeRegisteredEvent;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;
import org.axonframework.eventsourcing.annotation.reflection.EntityCreator;
import org.axonframework.extension.spring.stereotype.EventSourced;
import org.axonframework.messaging.commandhandling.annotation.CommandHandler;

@EventSourced(tagKey = "bikeId")
public class DecisionModel {

    Boolean registered = false;

    @EntityCreator
    public DecisionModel(){}

    @EventSourcingHandler
    public void handle(BikeRegisteredEvent event) {
        this.registered = true;
    }
}
