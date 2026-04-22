package io.axoniq.demo.bikerental.domain;

import io.axoniq.demo.bikerental.commands.*;
import io.axoniq.demo.bikerental.events.*;
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule;
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer;
import org.axonframework.messaging.commandhandling.configuration.CommandHandlingModule;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.fixture.AxonTestFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.axonframework.test.matchers.Matchers.andNoMore;
import static org.axonframework.test.matchers.Matchers.exactSequenceOf;
import static org.axonframework.test.matchers.Matchers.matches;
import static org.axonframework.test.matchers.Matchers.messageWithPayload;

class BikeTest {

    private AggregateTestFixture<Bike> fixture;

    /*
    @BeforeEach
    void setUp() {
        var bikeModule = EventSourcedEntityModule
                .autodetected(String.class, Bike.class);
        var commandHandlerModule = CommandHandlingModule.named("Rental")
                .commandHandlers().autodetectedCommandHandlingComponent(c -> new BikeCommands());
        var configurer = EventSourcingConfigurer.create()
                .modelling(c -> c.messaging(m -> m.registerCommandHandlingModule(commandHandlerModule)))
                .registerEntity(bikeModule);
        fixture = AxonTestFixture.with(configurer, AxonTestFixture.Customization::disableAxonServer);
    }

    @AfterEach
    void tearDown() {+
            fixture.stop();
    }
    */

    // https://miro.com/app/board/uXjVGl17uZw=/?moveToWidget=3458764668702272970&cot=14
    @Test
    void shouldRegisterBike() {
        fixture.givenNoPriorActivity()
               .when(new RegisterBikeCommand("bikeId", "city", "Amsterdam"))
               .expectEvents(new BikeRegisteredEvent("bikeId", "city", "Amsterdam"));
    }

    @Test
    void shouldRequestAvailableBike() {
        var rentalReference = UUID.randomUUID().toString();
        fixture.given(new BikeRegisteredEvent("bikeId", "city", "Amsterdam"))
               .when(new RequestBikeCommand("bikeId", "rider", rentalReference))
               .expectResultMessagePayloadMatching(matches(String.class::isInstance))
               .expectEventsMatching(exactSequenceOf(
                       messageWithPayload(matches((BikeRequestedEvent e) ->
                                                          e.bikeId().equals("bikeId")
                                                                  && e.renter().equals("rider")
                                                            && e.rentalReference().equals(rentalReference))),
                       andNoMore()));
    }

    @Test
    void shouldNotRequestAlreadyRequestedBike() {
        fixture.given(new BikeRegisteredEvent("bikeId", "city", "Amsterdam"),
                      new BikeRequestedEvent("bikeId", "rider", "rentalId"))
               .when(new RequestBikeCommand("bikeId", "rider", UUID.randomUUID().toString()))
               .expectNoEvents()
               .expectException(IllegalStateException.class);

    }

}