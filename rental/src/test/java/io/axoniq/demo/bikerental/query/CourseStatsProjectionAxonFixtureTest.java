package io.axoniq.demo.bikerental.query;

/*
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer;
import org.axonframework.messaging.eventhandling.configuration.EventProcessorModule;
import org.axonframework.messaging.eventhandling.processing.streaming.pooled.PooledStreamingEventProcessorModule;
import org.axonframework.messaging.queryhandling.configuration.QueryHandlingModule;
import org.axonframework.test.fixture.AxonTestFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class CourseStatsProjectionAxonFixtureTest {

    private AxonTestFixture fixture;

    private BikeStatusRepository bikeStatusRepository;

    @BeforeEach
    void beforeEach() {

        bikeStatusRepository = mock(BikeStatusRepository.class);

        PooledStreamingEventProcessorModule projectionProcessor = EventProcessorModule
                .pooledStreaming("BikeStatusProjection")
                .eventHandlingComponents(
                        c -> c.autodetected("BikeStatusProjection", cfg -> new BikeStatusProjection(bikeStatusRepository))
                ).notCustomized();

        QueryHandlingModule getCourseStatsByIdQueryHandler = QueryHandlingModule.named("QueryHandlingProjection")
                .queryHandlers()
                .autodetectedQueryHandlingComponent(cfg -> new BikeStatusProjection(bikeStatusRepository))
                .build();

        var configurer = EventSourcingConfigurer.create()
                .componentRegistry(cr -> cr.registerComponent(BikeStatusRepository.class, cfg -> bikeStatusRepository))
                .registerQueryHandlingModule(getCourseStatsByIdQueryHandler)
                .modelling(modelling -> modelling.messaging(messaging -> messaging.eventProcessing(eventProcessing ->
                        eventProcessing.pooledStreaming(ps -> ps.processor(projectionProcessor))
                )));

        this.fixture = AxonTestFixture.with(configurer, AxonTestFixture.Customization::disableAxonServer);
    }

    @AfterEach
    void afterEach() {
        fixture.stop();
    }

    @Test
    void givenNotExistingCourse_WhenGetById_ThenNotFound() {
        fixture.when()
                .nothing()
                .then()
                .expect(cfg -> assertCourseStatsNotExist());
    }

    private void assertCourseStatsNotExist() {
        assertTrue(bikeStatusRepository.findAll().isEmpty());
    }


}*/