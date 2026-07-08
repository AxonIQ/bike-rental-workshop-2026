package io.axoniq.demo.bikerental.ui;

import com.google.common.collect.Lists;
import io.axoniq.demo.bikerental.commands.*;
import io.axoniq.demo.bikerental.commands.requestbike.RequestBikeCommand;
import io.axoniq.demo.bikerental.query.FindAllBikes;
import io.axoniq.demo.bikerental.query.FindOneBike;
import org.axonframework.messaging.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.queryhandling.gateway.QueryGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final QueryGateway queryGateway;

    public RentalController(CommandGateway commandGateway, QueryGateway queryGateway) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @PostMapping
    public CompletableFuture<Void> generateBikes(@RequestParam(value = "bikeType") String bikeType) {
        CompletableFuture<Void> all = CompletableFuture.completedFuture(null);
        for (int i = 0; i < BIKES.size(); i++) {
            all = CompletableFuture.allOf(all,
                                          commandGateway.send(new RegisterBikeCommand(BIKES.get(i).toString(), bikeType, randomLocation())).resultAs(Void.class));
        }
        return all;
    }

    @PostMapping("/randomBike")
    public CompletableFuture<String> randomBike(@RequestParam(value = "bikeType") String bikeType) {
        return commandGateway.send(new RegisterBikeCommand(UUID.randomUUID().toString(), bikeType, randomLocation())).resultAs(String.class);
    }

    @PostMapping("/requestBike")
    public CompletableFuture<String> requestBike(@RequestParam("bikeId") String bikeId, @RequestParam("renter") String renter) {
        return commandGateway.send(new RequestBikeCommand(bikeId, renter, UUID.randomUUID().toString())).resultAs(String.class);
    }

    @PostMapping("/returnBike")
    public CompletableFuture<String> returnBike(@RequestParam("bikeId") String bikeId, @RequestParam("location") String location) {
        return commandGateway.send(new ReturnBikeCommand(bikeId, location != null ? location : randomLocation())).resultAs(String.class);
    }

    @PostMapping("/markDamaged/{bikeId}")
    public CompletableFuture<String> markBikeDamaged(@RequestParam("bikeId") String bikeId) {
        return commandGateway.send(new MarkBikeDamaged(bikeId)).resultAs(String.class);
    }

    @GetMapping("/bikes")
    public CompletableFuture<List<BikeStatus>> findAll() {
        return queryGateway.queryMany(new FindAllBikes(), BikeStatus.class);
    }

    @GetMapping("/bikes/{bikeId}")
    public CompletableFuture<BikeStatus> findStatus(@PathVariable("bikeId") String bikeId) {
        return queryGateway.query(new FindOneBike(bikeId), BikeStatus.class);
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
