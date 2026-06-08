# Building event-driven applications using DDD, CQRS and Event Sourcing, with Axon Framework

Welcome to this workshop! You will build a "bike rental" sample application
with [Axon Framework and Axon Server](https://developer.axoniq.io/).

## Quick Start Guide

### Prerequisites

* JDK version 17 or higher (currently using JDK 25)
* Maven 3.6+ (or use the included Maven wrapper `./mvnw`)
* Docker (optional, for running Axon Server)

### Running the Application

#### 1. Start Axon Server

Axon Server is the event store and message routing infrastructure. Start it using Docker:

```bash
docker run -d --name axonserver -p 8024:8024 -p 8124:8124 \
  -e AXONIQ_AXONSERVER_DEVMODE_ENABLED=true \
  -e AXONIQ_AXONSERVER_STANDALONE=true \
  axoniq/axonserver
```

Or if you already have the container:

```bash
docker start axonserver
```

**Axon Server Dashboard**: http://localhost:8024

The dashboard provides:
- Overview of connected applications
- Event Store browser (Search events)
- Command and Query monitoring
- Application health and metrics

#### 2. Build the Project

From the project root directory:

```bash
./mvnw clean install
```

Or if you have Maven installed:

```bash
mvn clean install
```

#### 3. Start the Rental Application

```bash
cd rental
../mvnw spring-boot:run
```

Or using your IDE:
- Open `rental/src/main/java/io/axoniq/demo/bikerental/RentalApplication.java`
- Run the `main` method

The application will start on **port 8080** by default.

#### 4. Start the Payment Application (for full functionality)

In a separate terminal:

```bash
cd payment
../mvnw spring-boot:run
```

### Accessing the Application

#### Web UI

**URL**: http://localhost:8080

The bike rental UI provides:
- **Register Random Bike**: Add new bikes to the system with a specified type
- **Request Bike**: Rent a bike by entering bike ID and your name
- **Return Bike**: Return a bike to any available location
- **Live Bike Grid**: View all bikes with real-time status updates
  - Status badges: Available (green), Rented (red), Requested (orange)
  - Quick actions: Rent or return bikes directly from the grid

#### REST API Endpoints

The application exposes the following REST endpoints:

**Bike Management:**
- `POST /randomBike?bikeType={type}` - Register a new bike with random ID
- `GET /bikes` - Get all bikes with their current status
- `GET /bikes/{bikeId}` - Get status of a specific bike

**Rental Operations:**
- `POST /requestBike?bikeId={id}&renter={name}` - Request to rent a bike
- `POST /returnBike?bikeId={id}&location={location}` - Return a bike

**Example requests** are available in `requests.http` file (can be executed in IntelliJ IDEA).

#### API Documentation (Swagger/OpenAPI)

Currently, Swagger/OpenAPI is **not configured** in this project. To add it, you would need to:

1. Add dependency to `rental/pom.xml`:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webflux-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

2. Access Swagger UI at: http://localhost:8080/swagger-ui.html

### How It Works

This application demonstrates **Event Sourcing**, **CQRS**, and **Saga Pattern** using Axon Framework:

#### Architecture Components

1. **Command Side (Write Model)**
   - `Bike` aggregate handles commands and publishes events
   - Commands: `RegisterBikeCommand`, `RequestBikeCommand`, `ReturnBikeCommand`, `ApproveBikeRequestCommand`
   - Events: `BikeRegisteredEvent`, `BikeRequestedEvent`, `BikeReturnedEvent`, `BikeRequestApprovedEvent`

2. **Query Side (Read Model)**
   - `BikeStatusProjection` maintains the current state of bikes in H2 database
   - Handles queries: `FindAllBikes`, `FindOneBike`
   - Returns `BikeStatus` with current location, renter, and status

3. **Process Manager (Saga)**
   - `PaymentSaga` coordinates the rental process across Rental and Payment contexts
   - Ensures payment is confirmed before approving bike rental
   - Demonstrates distributed transaction handling

4. **Event Store**
   - Axon Server stores all events
   - Provides event replay capability
   - Enables time-travel debugging and audit trails

#### Event Flow Example

When you request a bike:

1. `RequestBikeCommand` → `Bike` aggregate
2. `BikeRequestedEvent` published
3. `PaymentSaga` receives event and sends `PreparePaymentCommand`
4. Payment service confirms payment → `PaymentConfirmedEvent`
5. `PaymentSaga` receives confirmation and sends `ApproveBikeRequestCommand`
6. `Bike` aggregate processes approval → `BikeRequestApprovedEvent`
7. `BikeStatusProjection` updates bike status to "RENTED"
8. UI reflects the new state

### Troubleshooting

**Port already in use**: If port 8080 is already in use, you can change it by creating `rental/src/main/resources/application.properties`:
```properties
server.port=8081
```

**Axon Server connection issues**: Ensure Axon Server is running and accessible at `localhost:8024`

**Database issues**: Delete the `*.db` files in the project root and restart the application

### Stopping the Application

```bash
# Stop Axon Server
docker stop axonserver

# Stop Spring Boot applications
# Press Ctrl+C in the terminal where they're running
```

---

## Installation

The following software must be installed in your local environment:

* JDK version 17 (or higher).

* Axon Server, which is available to download as a standalone JAR file or as a Docker Image.

    * To install as a Jar file:

      First, [Download Axon Server](https://developer.axoniq.io/download). In the Axon Server directory, next
      to `AxonServer.jar`, create a file called `axonserver.properties` and add the following lines:

          axoniq.axonserver.devmode.enabled=true
          axoniq.axonserver.standalone=true

      Run Axon Server (@ `localhost:8024`):

          java -jar axonserver.jar

    * To run Axon Server using Docker:

          docker run -d --name axonserver -p 8024:8024 -p 8124:8124 -e AXONIQ_AXONSERVER_DEVMODE_ENABLED=true -e AXONIQ_AXONSERVER_STANDALONE=true axoniq/axonserver

      You can then start and stop Axon Server respectively by running:

          docker start axonserver
          docker stop axonserver

* An IDE such as [Jetbrains IDEA](https://www.jetbrains.com/idea/) is recommended.

## Part 1: Building Rental monolith

The workshop consists of a number of exercises to be completed in sequence, thus building the `rental` app and
connecting its process to the `payment` application, which has already been built.

In this exercise, the command model of the `rental` boundary context will be implemented.

An event-sourced aggregate `Bike` (representing the domain entity "bike") accepts and validates incoming commands,
publishing corresponding events that are persisted in the Axon Server event store.

Follow the steps outlined below.

### Exercise 1 - Creating the Bike aggregate

The (failing) `shouldRegisterBike` test in `BikeTest` attempts to send a `RegisterBikeCommand` to (a newly
created) `Bike` aggregate, expecting
a `BikeRegisteredEvent` to be published as a result.

* Annotate the `Bike` class as an aggregate via `@Aggregate`.

* The aggregate needs a unique identifier; use the `@AggregateIdentifier` annotation on a `String` member field
  called `bikeId`.

* Create a _command handling constructor_, annotated with `CommandHandler`, that accepts `RegisterBikeCommand`; also add
  a no-arg constructor (required).

* Publish a `BikeRegisteredEvent` in the command handling constructor via the statically imported `apply` function.

* Create an _event sourcing handler_ that accepts `BikeRegisteredEvent` and performs the actual state change; in this
  case, setting the aggregate's identifier. An event sourcing handler is a method (choose any name) that is annotated
  with the `@EventSourcingHandler` annotation.

> For more information on implementing aggregates,
> see [here](https://docs.axoniq.io/reference-guide/v/4.6/axon-framework/axon-framework-commands/modeling/aggregate).

Run the test to confirm that you've created the constructor properly.

#### Implementing all Commands and Events

The remaining unit tests cover the other commands and events related to `Bike`.

Implement the required Command Handlers and Event Sourcing handlers to make all the tests pass.

> Note
>
> Unlike in the first exercise, these handlers should act on an existing aggregate instance, rather than creating
> a new one. Therefore, create a regular instance method (the ones with a name and return value) instead of a
> constructor.
> The best practice is to use `void` as return type, unless you explicitly expect to return a value from the command's
> execution.

Note that the `RequestBikeCommand` expects a return value. This is the "rental reference", which must be a unique
value to be able to refer to a specific attempt to rent a bike. This reference will later be used to link this request
to the payments. You should return the same value that is also used in the `BikeRequestedEvent`.

#### Connecting the Commands to the UI controller

Commands and queries are sent from the `RentalController` using a `CommandGateway` and `QueryGateway` respectively.

Implement the (POST) `/requestBike` and `/returnBike` endpoints by sending the corresponding commands; see
the `generateBikes` method for how to use `CommandGateway`.

### Exercise 2 - Projections

Queries are handled by a query model (a.k.a. _projections_). A projection is updated when events are published, and it
implements so-called _query handlers_ that receive and respond to queries (sent via the `QueryGateway`).

A `BikeStatusProjection` class exists that needs the following:

* Event handlers for each of the events currently supported; an event handler is annotated with `@EventHandler` and
  updates bike statuses in an embedded database.

  > Don't confuse `@EventHandler` with `@EventSourcingHandler`! The latter only exists on the command-side.

  Define an Event Handler method for each of the events you need to process. Put the event's type as the first
  parameter. Generally, you won't need other parameters.

  For example::

  ```
  @EventHandler
  public void handle(BikeRegisteredEvent event) {
     // event handling logic here
  }
  ```

  `BikeStatusRepository` offers methods for persisting (`save`) and retrieving instances (`findById`, `findAll`)
  of `BikeStatus`.

  > `BikeStatus` contains methods to set relevant fields; you can find and update a particular bike status via
  >
  >       bikeStatusRepository.findById(someBikeId).ifPresent(
  >        bikeStatus -> { 
  >           // update bikeStatus...
  >        }
  >       )`

* Query handlers are annotated with `@QueryHandler(queryName = "someQuery")` and use `BikeStatusRepository` to retrieve
  and return results (that conform to what is expected in `BikeController`). Define a Query Handler method for each of
  the queries you need to provide. Put the query's payload type as the first parameter. Generally, you won't need other
  parameters.

  For example::
  ```
  @QueryHandler(queryName="myQueryName")
  public QueryResponse handle(MyQuery event) {
     // query handling logic here
  }
  ```

In the `RentalController`, implement the (GET) `/bikes` and `/bikes/{bikeId}` endpoints by sending the corresponding
queries:

* GET `/bikes`:

  `queryGateway.query("findAll", null, ResponseTypes.multipleInstancesOf(BikeStatus.class));`

* GET `/bikes/{bikeId}`:

  `queryGateway.query("findOne", bikeId, BikeStatus.class);`

> View
>
the [JavaDoc](https://apidocs.axoniq.io/latest/org/axonframework/queryhandling/QueryGateway.html#query-java.lang.String-Q-org.axonframework.messaging.responsetypes.ResponseType-)
> for sending queries.

#### Running the Application with Axon Server

Open your web browser and navigate to `localhost:8024` for the Administrator UI.

Start the Bike application; you will see the application connected to Axon Server in the _Overview_ section.

Perform the following tasks and inspect the _Search_ (for events), _Commands_ and _Queries_ sections:

The requests to send are available in the `requests.http` file. You may have to change the parameter values for each
request.

* Register a number of bikes.

* Get all registered bikes.

* Request a specific bike.

* Get the status of the requested bike. It should show that it has been requested.

Close the Bike application; reset the event store in the _Settings_ section and remove the `*.db` files that have been
created by H2 in the project's main directory.

### Exercise 3: Connecting the Payment processing

In order to successfully rent a bike, a payment must be made before the bike request is approved. Since this is a
transaction that spans both `rental` and `payment` contexts, a _process_ (also called _saga_) must be initiated.

The payment process is implemented in `PaymentSaga`; it starts and ends upon receiving a `BikeRequestedEvent`
and `PaymentConfirmedEvent` respectively.

Implement the process as follows:

* Annotate a member function that accepts a `BikeRequestedEvent` with

        @StartSaga
        @SagaEventHandler(associationId = "bikeId")

* In this event handler:

  Store the bike ID as process state (simply a member field, which will be used later).

  Generate a new payment ID (for example using `UUID.randomUUID.toString()`) and associated it with the process via:

        SagaLifecycle.associatedWith("paymentId", paymentId)

  Send a `PreparePaymentCommand` via the `CommandGateway`. THe handler for this command is implemented in the Payment
  Application.

* Annotate a member function that accepts a `PaymentConfirmedEvent` with

        @EndSaga
        @SagaEventHandler(associationId = "paymentId")

  Send a `ApproveBikeRequestCommand`.

> Why can't the `@EndSaga` event handler be associated with `bikeId`?

> Why didn't we need a "prepare payment" endpoint in `PaymentController`?

#### Running the Application with Axon Server

> If Axon Server is not currently running, start it again. Make sure to reset the event store.

Start both the `RentalApplication` and the `PaymentApplication`

Perform the following tasks and inspect the _Search_ (for events), _Commands_ and _Queries_ sections:

The requests to send are available in the `requests.http` file. You may have to change the parameter values for each
request.

* Register a number of bikes.

* Get all registered bikes.

* Request a specific bike. Note the rental reference.

* Find the payment ID for the rental reference and confirm that payment.

* Verify that the payment has been approved and the bike is now in use.

* Return the bike in a new location

## Part 2: Scaling out

Our monolith now consists of 4 different components, all within the same application:

* Command component (the `Bike`)
* Query Handlers (the `BikeStatusProjection` and `BikeStatusRepository`)
* Payment coordination (the `PaymentSaga`)
* UI Controller (the `RentalController`)

Let's move each of those classes to their own module. These modules have already been prepared under
the `/microservices` folder in the project.

Move all the classes mentioned above to their respective modules. Note that the package names remain the same. Using
the "move" function of IntelliJ allows you to simply choose a different source root in the dropdown in the lower part of
the "move" window.

Once moved, start each application:

* `RentalCommandApplication`
* `RentalQueryApplication`
* `UserInterfaceApplication`
* `RentalPaymentSagaApplication`
* `PaymentApplication`

Check the Axon Server UI to make sure all applications are started.

Go through the rental process again to validate that everything still works.

Try starting a few extra instances of the microservices. Note that the `UserInterfaceApplication` binds to port 8080, so
you can only start one instance. Check the `Commands` and `Queries` screens on the Axon Server UI to see how commands
and queries are balanced between nodes.

## When you're done

Congratulations! You've implemented the core concepts. But there is a lot more to explore. In these labs, we've
only covered the basics of the functional components of Axon Framework. There are a lot of non-functional configuration
items hidden in this application.

You can take a look at the [Bike Rental Demo application](https://github.com/abuijze/bike-rental-extended) on GitHub fur
the full implementation, including deadlines and subscription queries.