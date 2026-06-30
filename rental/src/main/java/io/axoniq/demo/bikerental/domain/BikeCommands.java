package io.axoniq.demo.bikerental.domain;

import io.axoniq.demo.bikerental.commands.*;
import io.axoniq.demo.bikerental.events.*;
import org.axonframework.messaging.commandhandling.annotation.Command;
import org.axonframework.messaging.commandhandling.annotation.CommandHandler;
import org.axonframework.messaging.core.unitofwork.ProcessingContext;
import org.axonframework.messaging.eventhandling.gateway.EventAppender;
import org.axonframework.modelling.annotation.InjectEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class BikeCommands {


    //https://miro.com/app/board/uXjVGl17uZw=/?moveToWidget=3458764666925523340&cot=14
    @CommandHandler
    public static String handle(RegisterBikeCommand command, EventAppender appender, ProcessingContext context) {
        appender.append(new BikeRegisteredEvent(command.bikeId(), command.bikeType(), command.location()));
        return command.bikeId();
    }

    //https://miro.com/app/board/uXjVGl17uZw=/?moveToWidget=3458764666925523319&cot=14
    @CommandHandler
    public String handle(RequestBikeCommand command, EventAppender appender, @InjectEntity Bike bike) throws Exception {
        if (!bike.isAvailable() || bike.isDamaged()) {
            throw new IllegalStateException("Bike is already rented");
        }
        appender.append(new BikeRequestedEvent(command.bikeId(), command.renter(), command.reference()));

        return command.reference();
    }

    //https://miro.com/app/board/uXjVGl17uZw=/?moveToWidget=3458764666925523344&cot=14
    @CommandHandler
    public void handle(ApproveRequestCommand command, EventAppender appender, @InjectEntity Bike bike) {
        if (!Objects.equals(bike.getReservedBy(), command.renter())
                || bike.isReservationConfirmed()) {
            return;
        }
        appender.append(new BikeInUseEvent(command.bikeId(), command.renter()));
    }

    @CommandHandler
    public void handle(RejectRequestCommand command, EventAppender appender, @InjectEntity Bike bike) {
        if (!Objects.equals(bike.getReservedBy(), command.renter())
                || bike.isReservationConfirmed()) {
            return;
        }
        appender.append(new RequestRejectedEvent(command.bikeId()));
    }

    //https://miro.com/app/board/uXjVGl17uZw=/?moveToWidget=3458764666925523347&cot=14
    @CommandHandler
    public void handle(ReturnBikeCommand command, EventAppender appender, @InjectEntity Bike bike) {
        if (bike.isAvailable()) {
            throw new IllegalStateException("Bike was already returned");
        }
        appender.append(new BikeReturnedEvent(command.bikeId(), command.location()));
    }

    @CommandHandler
    public void handle(MarkBikeDamaged command, EventAppender appender, @InjectEntity Bike bike) {
        if (bike == null) {
            throw new IllegalStateException("Uknown bike");
        }
        appender.append(new BikeMarkedDamagedEvent(command.bikeId()));
    }

}
