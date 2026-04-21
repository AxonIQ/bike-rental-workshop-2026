package io.axoniq.demo.bikerental.events;

import org.axonframework.serialization.Revision;

@Revision("0.0.1")
public record BikeRegisteredEvent(String bikeId, String bikeType, String location) {

}
