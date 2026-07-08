package io.axoniq.demo.courses.commands.subscribe;

import io.axoniq.demo.courses.commands.coursecreation.CreateCourseCommand;
import io.axoniq.demo.courses.commands.coursecreation.SetCourseLimitCommand;
import io.axoniq.demo.courses.domain.CourseCommands;
import io.axoniq.demo.courses.events.CourseCreatedEvent;
import io.axoniq.demo.courses.events.CourseLimitSetEvent;
import io.axoniq.demo.courses.events.SubscribedToCourseEvent;
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule;
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer;
import org.axonframework.messaging.commandhandling.configuration.CommandHandlingModule;
import org.axonframework.test.fixture.AxonTestFixture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CourseSubscriptionTest {

    private AxonTestFixture fixture;

    @BeforeEach
    void setUp() {
       /* var subscriptionDecisionModel = EventSourcedEntityModule
                .autodetected(SubscriptionId.class, CourseSubscriptionDecisionModel.class);*/

        var commandHandlerModule = CommandHandlingModule.named("Courses")
                .commandHandlers().autodetectedCommandHandlingComponent(c -> new CourseCommands());

        var configurer = EventSourcingConfigurer.create()
                .modelling(c -> c.messaging(m -> m.registerCommandHandlingModule(commandHandlerModule)));
        //.registerEntity(subscriptionDecisionModel);

        fixture = AxonTestFixture.with(configurer);
    }

    @AfterEach
    void tearDown() {
        fixture.stop();
    }

    @Test
    void shouldCreateCourse() {
        fixture.given()
                .noPriorActivity()
                .when()
                .command(new CreateCourseCommand("course-1", "Introduction to Event Sourcing"))
                .then()
                .events(new CourseCreatedEvent("course-1", "Introduction to Event Sourcing"));
    }

    @Test
    void shouldNotCreateDuplicateCourse() {
        fixture.given()
                .events(new CourseCreatedEvent("course-1", "Introduction to Event Sourcing"))
                .when()
                .command(new CreateCourseCommand("course-1", "Another Course"))
                .then()
                .exception(IllegalStateException.class);
    }

    @Test
    void shouldSetCourseLimit() {
        fixture.given()
                .events(new CourseCreatedEvent("course-1", "Introduction to Event Sourcing"))
                .when()
                .command(new SetCourseLimitCommand("course-1", 5))
                .then()
                .events(new CourseLimitSetEvent("course-1", 5));
    }

    @Test
    void shouldSubscribeToCourse() {
        fixture.given()
                .events(
                        new CourseCreatedEvent("course-1", "Introduction to Event Sourcing"),
                        new CourseLimitSetEvent("course-1", 5)
                )
                .when()
                .command(new SubscribeToCourseCommand("course-1", "student-1"))
                .then()
                .events(new SubscribedToCourseEvent("course-1", "student-1"));
    }

    @Test
    void shouldNotSubscribeToNonExistentCourse() {
        fixture.given()
                .noPriorActivity()
                .when()
                .command(new SubscribeToCourseCommand("course-999", "student-1"))
                .then()
                .exception(IllegalStateException.class);
    }

    @Test
    void shouldNotSubscribeWhenCourseIsAtCapacity() {
        fixture.given()
                .events(
                        new CourseCreatedEvent("course-1", "Introduction to Event Sourcing"),
                        new CourseLimitSetEvent("course-1", 2),
                        new SubscribedToCourseEvent("course-1", "student-1"),
                        new SubscribedToCourseEvent("course-1", "student-2")
                )
                .when()
                .command(new SubscribeToCourseCommand("course-1", "student-3"))
                .then()
                .exception(IllegalStateException.class);
    }

    @Test
    void shouldNotSubscribeTwiceToSameCourse() {
        fixture.given()
                .events(
                        new CourseCreatedEvent("course-1", "Introduction to Event Sourcing"),
                        new CourseLimitSetEvent("course-1", 5),
                        new SubscribedToCourseEvent("course-1", "student-1")
                )
                .when()
                .command(new SubscribeToCourseCommand("course-1", "student-1"))
                .then()
                .exception(IllegalStateException.class);
    }

    @Test
    void shouldNotSubscribeWhenStudentHasReachedMaxCourses() {
        fixture.given()
                .events(
                        // Create 11 courses
                        new CourseCreatedEvent("course-1", "Course 1"),
                        new CourseLimitSetEvent("course-1", 50),
                        new CourseCreatedEvent("course-2", "Course 2"),
                        new CourseLimitSetEvent("course-2", 50),
                        new CourseCreatedEvent("course-3", "Course 3"),
                        new CourseLimitSetEvent("course-3", 50),
                        new CourseCreatedEvent("course-4", "Course 4"),
                        new CourseLimitSetEvent("course-4", 50),
                        new CourseCreatedEvent("course-5", "Course 5"),
                        new CourseLimitSetEvent("course-5", 50),
                        new CourseCreatedEvent("course-6", "Course 6"),
                        new CourseLimitSetEvent("course-6", 50),
                        new CourseCreatedEvent("course-7", "Course 7"),
                        new CourseLimitSetEvent("course-7", 50),
                        new CourseCreatedEvent("course-8", "Course 8"),
                        new CourseLimitSetEvent("course-8", 50),
                        new CourseCreatedEvent("course-9", "Course 9"),
                        new CourseLimitSetEvent("course-9", 50),
                        new CourseCreatedEvent("course-10", "Course 10"),
                        new CourseLimitSetEvent("course-10", 50),
                        new CourseCreatedEvent("course-11", "Course 11"),
                        new CourseLimitSetEvent("course-11", 50),
                        // Student subscribes to 10 courses
                        new SubscribedToCourseEvent("course-1", "student-1"),
                        new SubscribedToCourseEvent("course-2", "student-1"),
                        new SubscribedToCourseEvent("course-3", "student-1"),
                        new SubscribedToCourseEvent("course-4", "student-1"),
                        new SubscribedToCourseEvent("course-5", "student-1"),
                        new SubscribedToCourseEvent("course-6", "student-1"),
                        new SubscribedToCourseEvent("course-7", "student-1"),
                        new SubscribedToCourseEvent("course-8", "student-1"),
                        new SubscribedToCourseEvent("course-9", "student-1"),
                        new SubscribedToCourseEvent("course-10", "student-1")
                )
                .when()
                // Try to subscribe to 11th course
                .command(new SubscribeToCourseCommand("course-11", "student-1"))
                .then()
                .exception(IllegalStateException.class);
    }

    @Test
    void shouldAllowMultipleStudentsToSubscribeToSameCourse() {
        fixture.given()
                .events(
                        new CourseCreatedEvent("course-1", "Introduction to Event Sourcing"),
                        new CourseLimitSetEvent("course-1", 5),
                        new SubscribedToCourseEvent("course-1", "student-1"),
                        new SubscribedToCourseEvent("course-1", "student-2")
                )
                .when()
                .command(new SubscribeToCourseCommand("course-1", "student-3"))
                .then()
                .events(new SubscribedToCourseEvent("course-1", "student-3"));
    }

    @Test
    void shouldAllowStudentToSubscribeToMultipleCourses() {
        fixture.given()
                .events(
                        new CourseCreatedEvent("course-1", "Introduction to Event Sourcing"),
                        new CourseLimitSetEvent("course-1", 10),
                        new CourseCreatedEvent("course-2", "Advanced CQRS"),
                        new CourseLimitSetEvent("course-2", 10),
                        new SubscribedToCourseEvent("course-1", "student-1")
                )
                .when()
                .command(new SubscribeToCourseCommand("course-2", "student-1"))
                .then()
                .events(new SubscribedToCourseEvent("course-2", "student-1"));
    }

    @Test
    void shouldAccessRepositoriesViaExpect() {
        fixture.given()
                .events(
                        new CourseCreatedEvent("course-1", "Introduction to Event Sourcing"),
                        new CourseLimitSetEvent("course-1", 5),
                        new SubscribedToCourseEvent("course-1", "student-1"),
                        new SubscribedToCourseEvent("course-1", "student-2")
                )
                .when()
                .command(new SubscribeToCourseCommand("course-1", "student-3"))
                .then()
                .expect(config -> {
                    // Demonstrate accessing repositories via config.getComponents()
                    var repositories = config.getComponents(org.axonframework.modelling.repository.Repository.class);

                    // Verify we have 3 repositories registered
                    if (repositories.size() != 3) {
                        throw new AssertionError("Expected 3 repositories, found: " + repositories.size());
                    }

                    // Verify the CourseSubscriptionDecisionModel repository is registered with the correct compound key
                    String expectedKey = "io.axoniq.demo.courses.commands.subscribe.CourseSubscriptionDecisionModel#io.axoniq.demo.courses.commands.subscribe.SubscriptionId";
                    if (!repositories.containsKey(expectedKey)) {
                        throw new AssertionError("Expected repository key not found: " + expectedKey);
                    }

                    // Get the repository and verify it's not null
                    var repository = repositories.get(expectedKey);
                    if (repository == null) {
                        throw new AssertionError("Repository is null for key: " + expectedKey);
                    }

                    System.out.println("Successfully accessed " + repositories.size() + " repositories:");
                    repositories.keySet().forEach(key -> System.out.println("  - " + key));
                })
                .events(new SubscribedToCourseEvent("course-1", "student-3"));
    }
}
