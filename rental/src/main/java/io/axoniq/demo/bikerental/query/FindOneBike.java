package io.axoniq.demo.bikerental.query;

import org.axonframework.messaging.queryhandling.annotation.Query;

@Query(namespace = "rental", name = "FindOneBike")
public record FindOneBike(String bikeId) {
}
