package io.axoniq.demo.bikerental.paymentsaga;

/*
import io.axoniq.demo.bikerental.domain.BikeCommands;
import io.axoniq.demo.bikerental.events.BikeRequestedEvent;
import io.axoniq.demo.bikerental.paymentapi.payment.PreparePaymentCommand;
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule;
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer;
import org.axonframework.messaging.commandhandling.configuration.CommandHandlingModule;
import org.axonframework.messaging.eventhandling.configuration.EventProcessorModule;
import org.axonframework.messaging.eventhandling.processing.streaming.pooled.PooledStreamingEventProcessorModule;
import org.axonframework.test.fixture.AxonTestFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaymentSagaAxon5Test {

    private AxonTestFixture fixture;

    @BeforeEach
    void setUp() {
        var paymentSaga = EventSourcedEntityModule
                .autodetected(String.class, PaymentSaga.PaymentProcessState.class);

        var commandHandlerModule = CommandHandlingModule.named("PaymentSaga")
                .commandHandlers().autodetectedCommandHandlingComponent(c -> new BikeCommands());

        PooledStreamingEventProcessorModule sagaProcessor = EventProcessorModule
                .pooledStreaming("SagaProcessor")
                .eventHandlingComponents(
                        c -> c.autodetected("PaymentSaga", cfg -> new PaymentSaga())
                ).notCustomized();

        var configurer = EventSourcingConfigurer.create()
                .modelling(c -> c.messaging(m -> {
                    m.registerCommandHandlingModule(commandHandlerModule);
                }))
                .registerEntity(paymentSaga)
                .modelling(modelling -> modelling.messaging(messaging -> messaging.eventProcessing(eventProcessing ->
                        eventProcessing.pooledStreaming(ps -> ps.processor(sagaProcessor))
                )));
        fixture = AxonTestFixture.with(configurer, AxonTestFixture.Customization::disableAxonServer);
    }

    @AfterEach
    public void tearDown() {
        fixture.stop();
    }

    @Test
    void shouldStartSagaOnBikeRequested() {
        fixture.given()
                .events(new BikeRequestedEvent("bikeId", "renter", "payRef"))
                .then().
                commands(new PreparePaymentCommand(10, "payRef"));
    }


}*/