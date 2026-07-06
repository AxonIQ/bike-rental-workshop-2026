package io.axoniq.demo.courses.commands.coursecreation;

import org.axonframework.messaging.commandhandling.annotation.Command;
import org.axonframework.modelling.annotation.TargetEntityId;

@Command(namespace = "courses", name = "CreateCourseCommand", routingKey = "courseId")
public record CreateCourseCommand(
        @TargetEntityId
        String courseId,
        String courseName
) {
}
