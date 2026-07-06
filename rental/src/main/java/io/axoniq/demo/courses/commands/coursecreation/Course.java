package io.axoniq.demo.courses.commands.coursecreation;

import io.axoniq.demo.courses.events.*;
import org.axonframework.eventsourcing.annotation.EventCriteriaBuilder;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;
import org.axonframework.eventsourcing.annotation.reflection.EntityCreator;
import org.axonframework.extension.spring.stereotype.EventSourced;
import org.axonframework.messaging.eventstreaming.EventCriteria;
import org.axonframework.messaging.eventstreaming.Tag;

import java.util.HashSet;
import java.util.Set;

@EventSourced
public class Course {

    private String courseId;
    private String courseName;
    private Integer limit;
    private Set<String> subscribers = new HashSet<>();

    @EntityCreator
    public Course() {
    }

    public String getCourseId() {
        return courseId;
    }

    public Integer getLimit() {
        return limit;
    }

    public Set<String> getSubscribers() {
        return subscribers;
    }

    public boolean hasLimit() {
        return limit != null;
    }

    public boolean isAtCapacity() {
        return limit != null && subscribers.size() >= limit;
    }

    @EventSourcingHandler
    protected void handle(CourseCreatedEvent event) {
        this.courseId = event.courseId();
        this.courseName = event.courseName();
    }

    @EventSourcingHandler
    protected void handle(CourseLimitSetEvent event) {
        this.limit = event.limit();
    }

    @EventSourcingHandler
    protected void handle(SubscribedToCourseEvent event) {
        this.subscribers.add(event.subscriberId());
    }

    @EventCriteriaBuilder
    private static EventCriteria resolveCriteria(String courseId) {
        return EventCriteria
                .havingTags(Tag.of("courseId", courseId))
                .andBeingOneOfTypes(
                        CourseCreatedEvent.class.getName(),
                        CourseLimitSetEvent.class.getName(),
                        SubscribedToCourseEvent.class.getName()
                );
    }
}
