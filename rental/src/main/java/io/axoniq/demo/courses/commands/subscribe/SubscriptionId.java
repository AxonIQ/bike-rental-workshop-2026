package io.axoniq.demo.courses.commands.subscribe;

public record SubscriptionId(String courseId, String subscriberId) {

    public static SubscriptionId of(String courseId, String subscriberId) {
        return new SubscriptionId(courseId, subscriberId);
    }

}
