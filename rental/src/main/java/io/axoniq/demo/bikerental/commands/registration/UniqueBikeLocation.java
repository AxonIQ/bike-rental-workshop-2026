package io.axoniq.demo.bikerental.commands.registration;

import io.axoniq.demo.bikerental.events.BikeRegisteredEvent;
import org.axonframework.eventsourcing.annotation.EventCriteriaBuilder;
import org.axonframework.eventsourcing.annotation.EventSourcedEntity;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;
import org.axonframework.eventsourcing.annotation.reflection.EntityCreator;
import org.axonframework.extension.spring.stereotype.EventSourced;
import org.axonframework.messaging.eventstreaming.EventCriteria;

@EventSourced
public class UniqueBikeLocation {

    public boolean registered = false;

    @EntityCreator
    public UniqueBikeLocation() {
    }

    @EventSourcingHandler
    public void handle(BikeRegisteredEvent event) {
        registered = true;
    }

    @EventCriteriaBuilder
    private static EventCriteria resolve(BikeLocation identifier) {
        return EventCriteria.havingTags("bikeId", identifier.bikeId(),
                "location", identifier.location())
                .andBeingOneOfTypes(BikeRegisteredEvent.class.getName());
    }

}
