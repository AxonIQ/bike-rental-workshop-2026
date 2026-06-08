package io.axoniq.demo.bikerental.paymentsaga;

import io.axoniq.demo.bikerental.paymentapi.payment.*;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.annotation.DeadlineHandler;
import org.axonframework.messaging.Scope;
import org.axonframework.messaging.ScopeDescriptor;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

// https://miro.com/app/board/uXjVLYxXN6I=/?share_link_id=966036859520
@Saga
public class PaymentSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private transient DeadlineManager deadlineManager;

    private String bikeId;
    private String renter;

    @StartSaga
    @SagaEventHandler(associationProperty = "bikeId")
    public void on(BikeRequestedEvent event) {
        this.bikeId = event.bikeId();
        this.renter = event.renter();
        SagaLifecycle.associateWith("paymentReference", event.rentalReference());
        preparePayment(event.rentalReference());
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "paymentReference")
    public void on(PaymentConfirmedEvent event) {
        // we approve the bike request
        commandGateway.send(new ApproveRequestCommand(bikeId, renter));
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "paymentReference")
    public void on(PaymentRejectedEvent event) {
        commandGateway.send(new RejectRequestCommand(bikeId, renter));
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "bikeId")
    public void on(RequestRejectedEvent event) {
        deadlineManager.cancelAllWithinScope("cancelPayment");
    }

    @SagaEventHandler(associationProperty = "paymentReference")
    public void on(PaymentPreparedEvent event) {
        deadlineManager.schedule(Duration.ofSeconds(30), "cancelPayment", event.paymentId());
    }

    @DeadlineHandler(deadlineName = "cancelPayment")
    public void cancelPayment(String paymentId) {
        commandGateway.send(new RejectPaymentCommand(paymentId));
    }

    @DeadlineHandler(deadlineName = "retryPayment")
    public void preparePayment(String rentalReference) {
        ScopeDescriptor scope = Scope.describeCurrentScope();
        commandGateway.send(new PreparePaymentCommand(10, rentalReference))
                .whenComplete((r, e) -> {
                    if (e != null) {
                        deadlineManager.schedule(Duration.ofSeconds(5), "retryPayment", rentalReference, scope);
                    }
                });
    }

}
