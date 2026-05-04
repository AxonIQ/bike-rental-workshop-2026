package io.axoniq.demo.bikerental.events;


import org.axonframework.eventsourcing.annotation.EventTag;
import org.axonframework.messaging.eventhandling.annotation.Event;

@Event(namespace = "rental", name = "BikeInUseEvent", version = "0.0.1")
public record BikeInUseEvent(@EventTag String bikeId, String renter) {

}
