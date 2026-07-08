package io.axoniq.demo.bikerental.events;

import org.axonframework.eventsourcing.annotation.EventTag;

public record RequestRejectedEvent(@EventTag String bikeId) {

}
