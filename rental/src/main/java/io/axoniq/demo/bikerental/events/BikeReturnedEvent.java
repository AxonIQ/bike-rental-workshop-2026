package io.axoniq.demo.bikerental.events;

import org.axonframework.eventsourcing.annotation.EventTag;
import org.axonframework.messaging.eventhandling.annotation.Event;

public record BikeReturnedEvent(@EventTag String bikeId, String location) {}
