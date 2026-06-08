package io.axoniq.demo.bikerental.domain;

import io.axoniq.demo.bikerental.commands.RegisterBikeCommand;
import io.axoniq.demo.bikerental.events.BikeRegisteredEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;
@Aggregate
public class Bike {

    @AggregateIdentifier
    private String bikeId;

    public Bike() {
    }

    @CommandHandler
    public Bike(RegisterBikeCommand command) {
        apply(new BikeRegisteredEvent(command.bikeId(), command.bikeType(), command.location()));
    }

    @EventSourcingHandler
    protected void handle(BikeRegisteredEvent event) {
        this.bikeId = event.bikeId();
    }

}
