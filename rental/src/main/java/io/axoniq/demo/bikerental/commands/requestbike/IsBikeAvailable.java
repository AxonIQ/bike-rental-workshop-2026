package io.axoniq.demo.bikerental.commands.requestbike;

import io.axoniq.demo.bikerental.events.BikeMarkedDamagedEvent;
import io.axoniq.demo.bikerental.events.BikeRegisteredEvent;
import io.axoniq.demo.bikerental.events.BikeRequestedEvent;
import io.axoniq.demo.bikerental.events.BikeReturnedEvent;
import org.axonframework.eventsourcing.annotation.EventCriteriaBuilder;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;
import org.axonframework.eventsourcing.annotation.reflection.EntityCreator;
import org.axonframework.extension.spring.stereotype.EventSourced;
import org.axonframework.messaging.core.MessageTypeResolver;
import org.axonframework.messaging.eventstreaming.EventCriteria;

@EventSourced
public class IsBikeAvailable {

    public boolean bikeRented;
    public boolean bikeRegistered;
    public boolean bikeDamaged;

    @EntityCreator
    public IsBikeAvailable(){}

    @EventSourcingHandler
    public void handle(BikeRegisteredEvent event) {
        this.bikeRegistered = true;
    }

    @EventSourcingHandler
    public void handle(BikeRequestedEvent event) {
        this.bikeRented = true;
    }

    @EventSourcingHandler
    public void handle(BikeReturnedEvent event) {
        this.bikeRented = false;
    }

    @EventSourcingHandler
    public void handle(BikeMarkedDamagedEvent event) {
        this.bikeDamaged = true;
    }

    @EventCriteriaBuilder
    private static EventCriteria resolve(String bikeId, MessageTypeResolver messageTypeResolver){
        return EventCriteria.havingTags("bikeId",bikeId)
                .andBeingOneOfTypes(messageTypeResolver,
                        // give me all of them io.axoniq.demo.bikerental.events.BikeRegisteredEvent
                        BikeRegisteredEvent.class,
                        BikeRequestedEvent.class,
                        BikeReturnedEvent.class,
                        BikeMarkedDamagedEvent.class);
    }
}
