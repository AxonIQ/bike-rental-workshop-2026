package io.axoniq.demo.courses.commands.subscribe;

import io.axoniq.demo.courses.events.SubscribedToCourseEvent;
import org.axonframework.eventsourcing.annotation.EventCriteriaBuilder;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;
import org.axonframework.eventsourcing.annotation.reflection.EntityCreator;
import org.axonframework.extension.spring.stereotype.EventSourced;
import org.axonframework.messaging.eventstreaming.EventCriteria;

@EventSourced
public class StudentSubscriptions {

    public Integer subscribedCourses = 0;

    @EntityCreator
    public StudentSubscriptions() {}

    @EventSourcingHandler
    public void handle(SubscribedToCourseEvent event) {
        this.subscribedCourses ++;
    }

    @EventCriteriaBuilder
    public static EventCriteria resolve(SubscriptionId subscriptionId) {
        return EventCriteria.havingTags("subscriberId", subscriptionId.subscriberId())
                .andBeingOneOfTypes(SubscribedToCourseEvent.class.getName());
    }
}
