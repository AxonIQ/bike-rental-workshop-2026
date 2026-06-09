package io.axoniq.demo.bikerental.events;

import org.axonframework.serialization.Revision;

public record BikeRegisteredEvent(String bikeId, String bikeType, String location) {

}
