package io.axoniq.demo.bikerental.ui;

import com.google.common.collect.Lists;
import io.axoniq.demo.bikerental.commands.RegisterBikeCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/")
public class RentalController {

    private static final List<String> LOCATIONS = Arrays.asList("Amsterdam", "Paris", "Vilnius", "Barcelona", "London", "New York", "Toronto", "Berlin", "Milan", "Rome", "Belgrade");
    private final CommandGateway commandGateway;

    public RentalController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping("/randomBike")
    public CompletableFuture<String> randomBike(@RequestParam(value = "bikeType") String bikeType) {
        return commandGateway.send(new RegisterBikeCommand(UUID.randomUUID().toString(), bikeType, randomLocation()));
    }

    private String randomLocation() {
        return LOCATIONS.get(ThreadLocalRandom.current().nextInt(LOCATIONS.size()));
    }

}
