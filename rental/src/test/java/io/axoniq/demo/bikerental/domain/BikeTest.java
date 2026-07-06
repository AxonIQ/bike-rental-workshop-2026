package io.axoniq.demo.bikerental.domain;

import io.axoniq.demo.bikerental.commands.RegisterBikeCommand;
import io.axoniq.demo.bikerental.commands.RequestBikeCommand;
import io.axoniq.demo.bikerental.events.BikeMarkedDamagedEvent;
import io.axoniq.demo.bikerental.events.BikeRegisteredEvent;
import io.axoniq.demo.bikerental.events.BikeRequestedEvent;
import io.axoniq.demo.bikerental.events.BikeReturnedEvent;
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule;
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer;
import org.axonframework.messaging.commandhandling.configuration.CommandHandlingModule;
import org.axonframework.test.fixture.AxonTestFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class BikeTest {

    private AxonTestFixture fixture;

    @BeforeEach
    void setUp() {
        var bikeModule = EventSourcedEntityModule
                .autodetected(String.class, Bike.class);
        var decisionModel = EventSourcedEntityModule
                .autodetected(String.class, DecisionModel.class);
        var commandHandlerModule = CommandHandlingModule.named("Rental")
                .commandHandlers().autodetectedCommandHandlingComponent(c -> new BikeCommands());
        var configurer = EventSourcingConfigurer.create()
                .modelling(c -> c.messaging(m -> m.registerCommandHandlingModule(commandHandlerModule)))
                .registerEntity(bikeModule)
                .registerEntity(decisionModel);
        fixture = AxonTestFixture.with(configurer);
    }

    @AfterEach
    void tearDown() {
        fixture.stop();
    }

    @Test
    void ensuresUniqueness() {
        fixture.given()
                .events(new BikeRegisteredEvent("bike-123", "mountain", "amsterdam"))
                .events(new BikeRegisteredEvent("bike-456", "mountain", "amsterdam"))
                .when()
                .command(new RegisterBikeCommand("bike-123", "rennrad", "amsterdam"))
                .then()
                .exception(IllegalStateException.class);
    }

    // https://miro.com/app/board/uXjVGl17uZw=/?moveToWidget=3458764668702272970&cot=14
    @Test
    void shouldRegisterBike() {
        fixture.given()
                .noPriorActivity()
                .when()
                .command(new RegisterBikeCommand("bikeId", "city", "Amsterdam"))
                .then()
                .events(new BikeRegisteredEvent("bikeId", "city", "Amsterdam"));
    }

    @Test
    void shouldRequestAvailableBike() {
        var rentalReference = UUID.randomUUID().toString();
        fixture
                .given()
                .events(new BikeRegisteredEvent("bikeId", "city", "Amsterdam"))
                .when().command(new RequestBikeCommand("bikeId", "rider", rentalReference))
                .then()
                .events(new BikeRequestedEvent("bikeId", "rider", rentalReference));
    }

    @Test
    void shouldNotRequestAlreadyRequestedBike() {
        fixture.given().events(new BikeRegisteredEvent("bikeId", "city", "Amsterdam"),
                        new BikeRequestedEvent("bikeId", "rider", "rentalId"))
                .when().command(new RequestBikeCommand("bikeId", "rider", UUID.randomUUID().toString()))
                .then()
                .exception(IllegalStateException.class);

    }

    @Test
    void cannotRequestADamagedBike() {
        fixture.given().events(new BikeRegisteredEvent("bikeId", "city", "Amsterdam"),
                        new BikeMarkedDamagedEvent("bikeId"))
                .when().command(new RequestBikeCommand("bikeId", "rider", UUID.randomUUID().toString()))
                .then()
                .exception(IllegalStateException.class);

    }

    @Test
    void cannotRequestADamagedBikeAfterReturn() {
        fixture.given().events(new BikeRegisteredEvent("bikeId", "city", "Amsterdam"),
                        new BikeReturnedEvent("bikeId", "Amsterdam"),
                        new BikeMarkedDamagedEvent("bikeId"))
                .when().command(new RequestBikeCommand("bikeId", "rider", UUID.randomUUID().toString()))
                .then()
                .exception(IllegalStateException.class);

    }


}