package io.axoniq.demo.courses.events;

import org.axonframework.eventsourcing.annotation.EventTag;
import org.axonframework.messaging.eventhandling.annotation.Event;


@Event
public record SubscribedToCourseEvent(
        @EventTag String courseId,
        @EventTag String subscriberId) {
}
