package io.axoniq.demo.courses.domain;

import io.axoniq.demo.courses.commands.coursecreation.CreateCourseCommand;
import io.axoniq.demo.courses.commands.coursecreation.SetCourseLimitCommand;
import io.axoniq.demo.courses.commands.subscribe.StudentSubscriptions;
import io.axoniq.demo.courses.commands.subscribe.SubscribeToCourseCommand;
import io.axoniq.demo.courses.commands.subscribe.SubscriptionDecisionModel;
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
    public void handle(SubscribeToCourseCommand command, EventAppender appender, @InjectEntity SubscriptionDecisionModel dm, @InjectEntity StudentSubscriptions studentSubscriptions) {
        // course must be created
        // is course at capacity?
        // has student reached max courses?
        // is student already subscribed?
        if(dm.subscribedStudents.size() >= dm.courseLimit || dm.subscribedStudents.contains(command.subscriberId()) || studentSubscriptions.subscribedCourses >= 10) {
            throw new IllegalStateException();
        }
        appender.append(new SubscribedToCourseEvent(command.courseId(), command.subscriberId()));
    }
}
