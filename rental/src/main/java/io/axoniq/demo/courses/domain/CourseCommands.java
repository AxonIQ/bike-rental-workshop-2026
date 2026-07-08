package io.axoniq.demo.courses.domain;

import io.axoniq.demo.courses.commands.coursecreation.CreateCourseCommand;
import io.axoniq.demo.courses.commands.coursecreation.SetCourseLimitCommand;
import io.axoniq.demo.courses.commands.subscribe.SubscribeToCourseCommand;
import io.axoniq.demo.courses.events.CourseCreatedEvent;
import io.axoniq.demo.courses.events.CourseLimitSetEvent;
import io.axoniq.demo.courses.events.SubscribedToCourseEvent;
import org.axonframework.messaging.commandhandling.annotation.CommandHandler;
import org.axonframework.messaging.eventhandling.gateway.EventAppender;
import org.springframework.stereotype.Component;

@Component
public class CourseCommands {

    @CommandHandler
    public static String handle(CreateCourseCommand command, EventAppender appender) {
        // course already exists by title
        appender.append(new CourseCreatedEvent(command.courseId(), command.courseName()));
        return command.courseId();
    }

    @CommandHandler
    public void handle(SetCourseLimitCommand command, EventAppender appender) {
        // course must be created
        appender.append(new CourseLimitSetEvent(command.courseId(), command.limit()));
    }

    @CommandHandler
    public void handle(SubscribeToCourseCommand command, EventAppender appender) {
        // course must be created
        // is course at capacity?
        // has student reached max courses?
        // is student already subscribed?
        appender.append(new SubscribedToCourseEvent(command.courseId(), command.subscriberId()));
    }
}
