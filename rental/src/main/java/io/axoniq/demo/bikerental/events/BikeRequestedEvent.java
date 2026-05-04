package io.axoniq.demo.bikerental.events;

import org.axonframework.eventsourcing.annotation.EventTag;
import org.axonframework.messaging.eventhandling.annotation.Event;

@Event(namespace = "rental", name = "BikeRequestedEvent", version = "0.0.1")
public record BikeRequestedEvent(@EventTag String bikeId, String renter, String rentalReference) {

}
