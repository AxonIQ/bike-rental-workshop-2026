package io.axoniq.demo.bikerental.events;

import org.axonframework.eventsourcing.annotation.EventTag;
import org.axonframework.messaging.eventhandling.annotation.Event;

// With @Event + namespace => rental.BikeRegisteredEvent
// Without @Event + namespace => io.axoniq.demo.bikerental.events.BikeRegisteredEvent
@Event(namespace = "rental", name = "BikeRegisteredEvent")
public record BikeRegisteredEvent(
        @EventTag String bikeId,
        String bikeType,
        String location) {
}
