package io.axoniq.demo.courses.commands.subscribe;

import io.axoniq.demo.bikerental.events.BikeRegisteredEvent;
import io.axoniq.demo.courses.events.CourseLimitSetEvent;
import io.axoniq.demo.courses.events.SubscribedToCourseEvent;
import org.axonframework.eventsourcing.annotation.EventCriteriaBuilder;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;
import org.axonframework.eventsourcing.annotation.reflection.EntityCreator;
import org.axonframework.extension.spring.stereotype.EventSourced;
import org.axonframework.messaging.eventstreaming.EventCriteria;

import java.util.ArrayList;
import java.util.List;

@EventSourced
public class SubscriptionDecisionModel {

    public Integer courseLimit = 10;
    public List<String> subscribedStudents = new ArrayList<>();

    @EntityCreator
    public SubscriptionDecisionModel() {}

    @EventSourcingHandler
    public void handle(SubscribedToCourseEvent event) {
        this.subscribedStudents.add(event.subscriberId());
    }

    @EventSourcingHandler
    public void handle(CourseLimitSetEvent event) {
        this.courseLimit = event.limit();
    }

    @EventCriteriaBuilder
    private static EventCriteria resolve(SubscriptionId subscriptionId) {
        return EventCriteria.either(EventCriteria.havingTags("courseId", subscriptionId.courseId(),
                "subscriberId", subscriptionId.subscriberId()))
                .or(EventCriteria.havingTags("courseId", subscriptionId.courseId()).andBeingOneOfTypes(SubscribedToCourseEvent.class.getName(),
                        CourseLimitSetEvent.class.getName()));
    }

}
