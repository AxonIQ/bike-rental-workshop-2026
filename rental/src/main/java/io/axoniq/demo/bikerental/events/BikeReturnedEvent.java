package io.axoniq.demo.bikerental.events;

import org.axonframework.eventsourcing.annotation.EventTag;
import org.axonframework.messaging.eventhandling.annotation.Event;

@Event(namespace = "rental", name = "BikeReturnedEvent", version = "0.0.1")
public record BikeReturnedEvent(@EventTag String bikeId, String location) {}
