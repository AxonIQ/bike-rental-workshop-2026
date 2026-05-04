package io.axoniq.demo.bikerental.paymentsaga;

import io.axoniq.demo.bikerental.commands.ApproveRequestCommand;
import io.axoniq.demo.bikerental.domain.Bike;
import io.axoniq.demo.bikerental.events.BikeRequestedEvent;
import io.axoniq.demo.bikerental.commands.RejectRequestCommand;
import io.axoniq.demo.bikerental.events.RequestRejectedEvent;
import io.axoniq.demo.bikerental.paymentapi.payment.*;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;
import org.axonframework.eventsourcing.annotation.reflection.EntityCreator;
import org.axonframework.extension.spring.stereotype.EventSourced;
import org.axonframework.messaging.commandhandling.gateway.CommandDispatcher;
import org.axonframework.messaging.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.eventhandling.annotation.EventHandler;
import org.axonframework.modelling.annotation.InjectEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;

// https://miro.com/app/board/uXjVLYxXN6I=/?share_link_id=966036859520
@Component
public class PaymentSaga {

    //(associationProperty = "bikeId")
    @EventHandler
    public void on(BikeRequestedEvent event, @InjectEntity PaymentProcessState paymentProcessState, CommandDispatcher commandDispatcher) {
        commandDispatcher.send(new PreparePaymentCommand(10, event.rentalReference()));

    }

    //(associationProperty = "paymentReference")
    @EventHandler
    public void on(PaymentConfirmedEvent event, @InjectEntity PaymentProcessState paymentProcessState, CommandDispatcher commandDispatcher) {
        // we approve the bike request
        commandDispatcher.send(new ApproveRequestCommand(paymentProcessState.bikeId, paymentProcessState.renter));
    }

    //(associationProperty = "paymentReference")
    @EventHandler
    public void on(PaymentRejectedEvent event, @InjectEntity PaymentProcessState paymentProcessState, CommandDispatcher commandDispatcher) {
        commandDispatcher.send(new RejectRequestCommand(paymentProcessState.bikeId, paymentProcessState.renter));
    }

    /*
    @DeadlineHandler(deadlineName = "cancelPayment")
    public void cancelPayment(String paymentId) {
        commandGateway.send(new RejectPaymentCommand(paymentId));
    }*/

    public void preparePayment(String rentalReference) {
    }

    @EventSourced
    public static class PaymentProcessState {
        private String bikeId;
        private String renter;

        @EntityCreator
        PaymentProcessState(){}

        @EventSourcingHandler
        public void handle(BikeRequestedEvent event) {
            bikeId = event.bikeId();
            renter = event.renter();
        }
    }

}
