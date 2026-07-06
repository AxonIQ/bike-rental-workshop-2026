package io.axoniq.demo.courses.domain;

import io.axoniq.demo.courses.commands.coursecreation.CreateCourseCommand;
import io.axoniq.demo.courses.commands.coursecreation.SetCourseLimitCommand;
import io.axoniq.demo.courses.commands.subscribe.CourseSubscriptionDecisionModel;
import io.axoniq.demo.courses.commands.subscribe.SubscribeToCourseCommand;
import io.axoniq.demo.courses.events.CourseCreatedEvent;
import io.axoniq.demo.courses.events.CourseLimitSetEvent;
import io.axoniq.demo.courses.events.SubscribedToCourseEvent;
import org.axonframework.messaging.commandhandling.annotation.CommandHandler;
import org.axonframework.messaging.eventhandling.gateway.EventAppender;
import org.axonframework.modelling.annotation.InjectEntity;
import org.springframework.stereotype.Component;

@Component
public class CourseCommands {

    @CommandHandler
    public static String handle(CreateCourseCommand command, EventAppender appender, @InjectEntity(idProperty = "courseId") CourseDecisionModel courseDecisionModel) {
        if (courseDecisionModel.created) {
            throw new IllegalStateException("Course already exists");
        }
        appender.append(new CourseCreatedEvent(command.courseId(), command.courseName()));
        return command.courseId();
    }

    @CommandHandler
    public void handle(SetCourseLimitCommand command, EventAppender appender, @InjectEntity(idProperty = "courseId") CourseCapacityDecisionModel capacityModel) {
        if (!capacityModel.isCourseCreated()) {
            throw new IllegalStateException("Course does not exist");
        }
        if (capacityModel.hasLimit()) {
            throw new IllegalStateException("Course limit already set");
        }
        appender.append(new CourseLimitSetEvent(command.courseId(), command.limit()));
    }

    @CommandHandler
    public void handle(SubscribeToCourseCommand command, EventAppender appender,
                       @InjectEntity(idProperty = "subscriptionId") CourseSubscriptionDecisionModel subscriptionModel) {
        if (!subscriptionModel.isCourseCreated()) {
            throw new IllegalStateException("Course does not exist");
        }
        if (subscriptionModel.isAtCapacity()) {
            throw new IllegalStateException("Course is at capacity");
        }
        if (subscriptionModel.hasReachedMaxCourses()) {
            throw new IllegalStateException("Student has reached maximum number of courses (10)");
        }
        if (subscriptionModel.isSubscribed()) {
            throw new IllegalStateException("Already subscribed to course");
        }
        appender.append(new SubscribedToCourseEvent(command.courseId(), command.subscriberId()));
    }
}
