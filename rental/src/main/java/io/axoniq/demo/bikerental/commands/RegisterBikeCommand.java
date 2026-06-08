package io.axoniq.demo.bikerental.commands;

public record RegisterBikeCommand(
        String bikeId,
        String bikeType,
        String location
) {}
