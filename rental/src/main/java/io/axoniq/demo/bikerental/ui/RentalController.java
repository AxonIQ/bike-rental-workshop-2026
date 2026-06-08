package io.axoniq.demo.bikerental.ui;

import com.google.common.collect.Lists;
import io.axoniq.demo.bikerental.commands.RegisterBikeCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    public RentalController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public CompletableFuture<Void> generateBikes(@RequestParam(value = "bikeType") String bikeType) {
        CompletableFuture<Void> all = CompletableFuture.completedFuture(null);
        for (int i = 0; i < BIKES.size(); i++) {
            all = CompletableFuture.allOf(all,
                                          commandGateway.send(new RegisterBikeCommand(BIKES.get(i).toString(), bikeType, randomLocation())));
        }
        return all;
    }

    private String randomLocation() {
        return LOCATIONS.get(ThreadLocalRandom.current().nextInt(LOCATIONS.size()));
    }

    static List<UUID> BIKES = Lists.newArrayList(
            UUID.fromString("6ab2acb3-c26d-4da2-82f2-3fc657016d4d"),
            UUID.fromString("5a24c842-3f9a-4730-9f1d-cbc25064164b"),
            UUID.fromString("bb372c03-cbec-48df-902a-2a4cde8df728"),
            UUID.fromString("c7f6287f-927b-4b78-9c17-ff6d743762ae"),
            UUID.fromString("b4d7a0da-4665-4b54-93d4-a374fc0a58ec")
    );
}
