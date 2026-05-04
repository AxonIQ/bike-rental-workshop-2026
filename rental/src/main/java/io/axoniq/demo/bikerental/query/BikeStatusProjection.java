package io.axoniq.demo.bikerental.query;

import io.axoniq.demo.bikerental.commands.BikeStatus;
import io.axoniq.demo.bikerental.commands.RentalStatus;
import io.axoniq.demo.bikerental.events.*;
import org.axonframework.messaging.eventhandling.annotation.EventHandler;
import org.axonframework.messaging.queryhandling.QueryUpdateEmitter;
import org.axonframework.messaging.queryhandling.annotation.QueryHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

// @SequencingPolicy(type = PropertySequencingPolicy.class, parameters = {"customerId"})

@Component
public class BikeStatusProjection {

    private final BikeStatusRepository bikeStatusRepository;

    public BikeStatusProjection(BikeStatusRepository bikeStatusRepository) {
        this.bikeStatusRepository = bikeStatusRepository;
    }

    @EventHandler
    public Mono<Void> on(BikeRegisteredEvent event, QueryUpdateEmitter updateEmitter) {

        return Mono.fromCallable(() -> {
                    var bikeStatus = new BikeStatus(
                        event.bikeId(),
                        event.bikeType(),
                        event.location()
                    );

                    bikeStatusRepository.save(bikeStatus); // blocking
                    return bikeStatus;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(bs ->
                    updateEmitter.emit(FindAllBikes.class, _ -> true, bs)
                )
                .then();
    }


    @EventHandler
    public void on(BikeRequestedEvent event,  QueryUpdateEmitter updateEmitter) {
        bikeStatusRepository.findById(event.bikeId())
                .map(bs -> {
                    bs.requestedBy(event.renter());
                    return bs;
                })
                .ifPresent(bs -> {
                    updateEmitter.emit(FindAllBikes.class, q -> true, bs);
                    updateEmitter.emit(FindOneBike.class, q->q.bikeId().equals(event.bikeId()), bs);
                });
    }

    @EventHandler
    public void on(BikeInUseEvent event, QueryUpdateEmitter updateEmitter) {
        bikeStatusRepository.findById(event.bikeId())
                .map(bs -> {
                    bs.rentedBy(event.renter());
                    return bs;
                })
                .ifPresent(bs -> {
                    updateEmitter.emit(FindAllBikes.class, q -> true, bs);
                    updateEmitter.emit(FindOneBike.class, q->q.bikeId().equals(event.bikeId()), bs);
                });
    }

    @EventHandler
    public void on(BikeReturnedEvent event, QueryUpdateEmitter updateEmitter) {
        bikeStatusRepository.findById(event.bikeId())
                .map(bs -> {
                    bs.returnedAt(event.location());
                    return bs;
                })
                .ifPresent(bs -> {
                    updateEmitter.emit(FindAllBikes.class, q -> true, bs);
                    updateEmitter.emit(FindOneBike.class, q->q.bikeId().equals(event.bikeId()), bs);
                });

    }

    @EventHandler
    public void on(RequestRejectedEvent event, QueryUpdateEmitter updateEmitter) {
        bikeStatusRepository.findById(event.bikeId())
                .map(bs -> {
                    bs.returnedAt(bs.getLocation());
                    return bs;
                })
                .ifPresent(bs -> {
                    updateEmitter.emit(FindAllBikes.class, q -> true, bs);
                    updateEmitter.emit(FindOneBike.class, q->q.bikeId().equals(event.bikeId()), bs);
                });
    }

    @QueryHandler(queryName = "findAll")
    public List<BikeStatus> findAll(FindAllBikes findAllBikes) {
        return bikeStatusRepository.findAll();
    }

    @QueryHandler(queryName = "findAvailable")
    public List<BikeStatus> findAvailable(String bikeType) {
        return bikeStatusRepository.findAllByBikeTypeAndStatus(bikeType, RentalStatus.AVAILABLE);
    }

    @QueryHandler(queryName = "findOne")
    public BikeStatus findOne(FindOneBike query) {
        return bikeStatusRepository.findById(query.bikeId()).orElse(null);
    }
}
