package io.axoniq.demo.bikerental.query;

import org.axonframework.messaging.queryhandling.annotation.Query;

@Query(namespace = "rental", name = "FindAllBikes")
public record FindAllBikes() {}
