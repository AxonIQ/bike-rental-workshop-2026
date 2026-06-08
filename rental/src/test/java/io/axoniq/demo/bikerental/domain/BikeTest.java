package io.axoniq.demo.bikerental.domain;

import io.axoniq.demo.bikerental.commands.*;
import io.axoniq.demo.bikerental.events.*;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.axonframework.test.matchers.Matchers.andNoMore;
import static org.axonframework.test.matchers.Matchers.exactSequenceOf;
import static org.axonframework.test.matchers.Matchers.matches;
import static org.axonframework.test.matchers.Matchers.messageWithPayload;

class BikeTest {

    private AggregateTestFixture<Bike> fixture;

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(Bike.class);
    }

    // https://miro.com/app/board/uXjVGl17uZw=/?moveToWidget=3458764668702272970&cot=14
    @Test
    void shouldRegisterBike() {
        fixture.givenNoPriorActivity()
               .when(new RegisterBikeCommand("bikeId", "city", "Amsterdam"))
               .expectEvents(new BikeRegisteredEvent("bikeId", "city", "Amsterdam"));
    }

}