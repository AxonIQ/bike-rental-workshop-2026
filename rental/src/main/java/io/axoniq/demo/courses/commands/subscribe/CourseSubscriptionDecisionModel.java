package io.axoniq.demo.courses.commands.subscribe;

import io.axoniq.demo.courses.events.CourseCreatedEvent;
import io.axoniq.demo.courses.events.CourseLimitSetEvent;
import io.axoniq.demo.courses.events.SubscribedToCourseEvent;
import org.axonframework.eventsourcing.annotation.EventCriteriaBuilder;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;
import org.axonframework.eventsourcing.annotation.reflection.EntityCreator;
import org.axonframework.eventsourcing.annotation.reflection.InjectEntityId;
import org.axonframework.extension.spring.stereotype.EventSourced;
import org.axonframework.messaging.eventstreaming.EventCriteria;
import org.axonframework.messaging.eventstreaming.Tag;

import java.util.HashSet;
import java.util.Set;

@EventSourced
public class CourseSubscriptionDecisionModel {

    private static final int MAX_COURSES_PER_STUDENT = 10;

    private final SubscriptionId subscriptionId;
    private boolean subscribed = false;
    private boolean courseCreated = false;
    private Integer limit = null;
    private int attendeeCount = 0;
    private Set<String> studentCourses = new HashSet<>();

    @EntityCreator
    public CourseSubscriptionDecisionModel(@InjectEntityId SubscriptionId subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public boolean isCourseCreated() {
        return courseCreated;
    }

    public boolean isAtCapacity() {
        return limit != null && attendeeCount >= limit;
    }

    public boolean hasReachedMaxCourses() {
        return studentCourses.size() >= MAX_COURSES_PER_STUDENT;
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
        // Count subscribers for this course's capacity
        if (event.courseId().equals(subscriptionId.courseId())) {
            this.attendeeCount++;
        }

        // Track all courses this student has subscribed to
        if (event.subscriberId().equals(subscriptionId.subscriberId())) {
            studentCourses.add(event.courseId());

            // Mark this specific subscription
            if (event.courseId().equals(subscriptionId.courseId())) {
                this.subscribed = true;
            }
        }
    }

    @EventCriteriaBuilder
    private static EventCriteria resolveCriteria(SubscriptionId subscriptionId) {
        // Source events for this course (capacity check) OR this subscriber (student's course count)
        return EventCriteria
                .havingTags("courseId", subscriptionId.courseId())
                .or()
                .havingTags("subscriberId", subscriptionId.subscriberId())
                .andBeingOneOfTypes(
                        CourseCreatedEvent.class.getName(),
                        CourseLimitSetEvent.class.getName(),
                        SubscribedToCourseEvent.class.getName()
                );
    }
}



