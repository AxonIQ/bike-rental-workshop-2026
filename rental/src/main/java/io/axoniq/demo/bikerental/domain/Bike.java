package io.axoniq.demo.bikerental.domain;

import io.axoniq.demo.bikerental.commands.*;
import io.axoniq.demo.bikerental.events.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.Objects;
import java.util.UUID;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;
// https://docs.axoniq.io/axon-framework-reference/5.1/migration/paths/aggregates/
@Aggregate
public class Bike {

    @AggregateIdentifier
    private String bikeId;

    private boolean isAvailable;
    private String reservedBy;
    private boolean reservationConfirmed;

    public Bike() {
    }

    //https://miro.com/app/board/uXjVGl17uZw=/?moveToWidget=3458764666925523340&cot=14
    @CommandHandler
    public Bike(RegisterBikeCommand command) {
        apply(new BikeRegisteredEvent(command.bikeId(), command.bikeType(), command.location()));
    }

    /**
     * TODO implememnt request bike
     */

}
