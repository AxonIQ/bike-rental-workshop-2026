package io.axoniq.demo.courses.domain;

import io.axoniq.demo.courses.events.CourseCreatedEvent;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;
import org.axonframework.eventsourcing.annotation.reflection.EntityCreator;
import org.axonframework.extension.spring.stereotype.EventSourced;

@EventSourced(tagKey = "courseId")
public class CourseDecisionModel {

    Boolean created = false;

    @EntityCreator
    public CourseDecisionModel() {
    }

    @EventSourcingHandler
    public void handle(CourseCreatedEvent event) {
        this.created = true;
    }
}
