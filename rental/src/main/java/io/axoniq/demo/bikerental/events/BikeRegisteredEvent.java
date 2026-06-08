package io.axoniq.demo.bikerental.events;

public record BikeRegisteredEvent(String bikeId, String bikeType, String location) {}
