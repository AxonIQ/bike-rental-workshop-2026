package io.axoniq.demo.courses.domain;

import io.axoniq.demo.courses.events.CourseCreatedEvent;
import io.axoniq.demo.courses.events.CourseLimitSetEvent;
import io.axoniq.demo.courses.events.SubscribedToCourseEvent;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;
import org.axonframework.eventsourcing.annotation.reflection.EntityCreator;
import org.axonframework.extension.spring.stereotype.EventSourced;

@EventSourced(tagKey = "courseId")
public class CourseCapacityDecisionModel {

    private boolean courseCreated = false;
    private Integer limit = null;
    private int attendeeCount = 0;

    @EntityCreator
    public CourseCapacityDecisionModel() {
    }

    public boolean isCourseCreated() {
        return courseCreated;
    }

    public boolean hasLimit() {
        return limit != null;
    }

    public boolean isAtCapacity() {
        return limit != null && attendeeCount >= limit;
    }

    public int getAttendeeCount() {
        return attendeeCount;
    }

    public Integer getLimit() {
        return limit;
    }

    @EventSourcingHandler
    public void handle(CourseCreatedEvent event) {
        this.courseCreated = true;
    }

    @EventSourcingHandler
    public void handle(CourseLimitSetEvent event) {
        this.limit = event.limit();
    }

    @EventSourcingHandler
    public void handle(SubscribedToCourseEvent event) {
        this.attendeeCount++;
    }
}
