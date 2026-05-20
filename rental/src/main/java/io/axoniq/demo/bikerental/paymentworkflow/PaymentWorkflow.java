package io.axoniq.demo.bikerental.paymentworkflow;

import io.axoniq.demo.bikerental.paymentapi.payment.PreparePaymentCommand;
import io.axoniq.workflow.dsl.simple.SimpleWorkflowContext;
import io.axoniq.workflow.runtime.api.annotation.Workflow;
import org.axonframework.messaging.commandhandling.gateway.CommandDispatcher;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class PaymentWorkflow {

    @Workflow(workflowName = "payment4", idProperty = "bikeId", startOnEvent = "rental.BikeRequestedEvent" )
    public void startWorkflow(SimpleWorkflowContext ctx) {
        ctx.awaitExecute("order", Boolean.class, ()->{
            System.out.println("Starting PaymentWorkflow");
            return true;
        });
        ctx.setDefaultTimeout(Duration.ofSeconds(10));
    }


}
