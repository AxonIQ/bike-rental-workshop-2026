package io.axoniq.demo.courses.commands.coursecreation;

import org.axonframework.messaging.commandhandling.annotation.Command;
import org.axonframework.modelling.annotation.TargetEntityId;

@Command(namespace = "courses", name = "SetCourseLimitCommand", routingKey = "courseId")
public record SetCourseLimitCommand(
        @TargetEntityId
        String courseId,
        int limit
) {
}
