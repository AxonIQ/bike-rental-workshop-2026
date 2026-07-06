package io.axoniq.demo.bikerental.domain;

import io.axoniq.demo.bikerental.events.*;
import org.axonframework.eventsourcing.annotation.EventCriteriaBuilder;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;
import org.axonframework.eventsourcing.annotation.reflection.EntityCreator;
import org.axonframework.extension.spring.stereotype.EventSourced;
import org.axonframework.messaging.eventstreaming.EventCriteria;
import org.axonframework.messaging.eventstreaming.Tag;

// https://docs.axoniq.io/axon-framework-reference/5.1/migration/paths/aggregates/
@EventSourced
public class Bike {

    private String bikeId;

    public String getBikeId() {
        return bikeId;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public String getReservedBy() {
        return reservedBy;
    }

    public boolean isDamaged() {
        return damaged;
    }

    private boolean isAvailable;
    private boolean damaged = false;
    private String reservedBy;

    public boolean isReservationConfirmed() {
        return reservationConfirmed;
    }

    private boolean reservationConfirmed;

    @EntityCreator
    public Bike() {
    }

    @EventSourcingHandler
    protected void handle(BikeRegisteredEvent event) {
        this.bikeId = event.bikeId();
        this.isAvailable = true;
    }

    @EventSourcingHandler
    protected void handle(BikeReturnedEvent event) {
        this.isAvailable = true;
        this.reservationConfirmed = false;
        this.reservedBy = null;
    }

    @EventSourcingHandler
    protected void handle(BikeRequestedEvent event) {
        this.reservedBy = event.renter();
        this.reservationConfirmed = false;
        this.isAvailable = false;
    }

    @EventSourcingHandler
    protected void handle(RequestRejectedEvent event) {
        this.reservedBy = null;
        this.reservationConfirmed = false;
        this.isAvailable = true;
    }

    @EventSourcingHandler
    protected void on(BikeInUseEvent event) {
        this.isAvailable = false;
        this.reservationConfirmed = true;
    }

    @EventSourcingHandler
    protected void handle(BikeMarkedDamagedEvent event) {
        this.damaged = true;
    }

    @EventCriteriaBuilder
    private static EventCriteria resolveCriteria(String bikeId) {
        return EventCriteria
                        .havingTags(Tag.of("bikeId", bikeId))
                        .andBeingOneOfTypes(
                                BikeRegisteredEvent.class.getName(),
                                BikeRequestedEvent.class.getName(),
                                BikeReturnedEvent.class.getName(),
                                BikeMarkedDamagedEvent.class.getName()
        );
    }
}
