package io.axoniq.demo.courses.commands.subscribe;

import org.axonframework.messaging.commandhandling.annotation.Command;
import org.axonframework.modelling.annotation.TargetEntityId;

@Command(namespace = "courses", name = "SubscribeToCourseCommand", routingKey = "courseId")
public record SubscribeToCourseCommand(
        String courseId,
        String subscriberId
) {
    @TargetEntityId
    public SubscriptionId subscriptionId() {
        return SubscriptionId.of(courseId, subscriberId);
    }
}
