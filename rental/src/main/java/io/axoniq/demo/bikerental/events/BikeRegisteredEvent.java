package io.axoniq.demo.bikerental.events;

import org.axonframework.eventsourcing.annotation.EventTag;
import org.axonframework.messaging.eventhandling.annotation.Event;

@Event
public record BikeRegisteredEvent(
        @EventTag String bikeId,
        String bikeType,
        String location) {
}
