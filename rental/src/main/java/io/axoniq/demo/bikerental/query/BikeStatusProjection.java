package io.axoniq.demo.bikerental.query;

import io.axoniq.demo.bikerental.commands.BikeStatus;
import io.axoniq.demo.bikerental.commands.RentalStatus;
import io.axoniq.demo.bikerental.events.*;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BikeStatusProjection {

    private final BikeStatusRepository bikeStatusRepository;

    public BikeStatusProjection(BikeStatusRepository bikeStatusRepository) {
        this.bikeStatusRepository = bikeStatusRepository;
    }

    // TODO define projection

}
