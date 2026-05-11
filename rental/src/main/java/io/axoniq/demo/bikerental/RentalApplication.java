package io.axoniq.demo.bikerental;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.axoniq.demo.bikerental.commands.BikeStatus;
import io.axoniq.demo.bikerental.common.DispatchTimeCommandDispatchInterceptor;
import io.axoniq.framework.axonserver.connector.api.AxonServerConnectionManager;
import io.axoniq.framework.axonserver.connector.event.AggregateBasedAxonServerEventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.messaging.eventhandling.conversion.EventConverter;
import org.axonframework.messaging.eventhandling.processing.streaming.token.store.jpa.TokenEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Import(AxonConfig.class)
@EntityScan(basePackageClasses = {BikeStatus.class, TokenEntry.class})
@SpringBootApplication
public class RentalApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentalApplication.class, args);
    }

    @Bean(destroyMethod = "shutdown")
    public ScheduledExecutorService workerExecutorService() {
        return Executors.newScheduledThreadPool(4);
    }

    @Autowired
    public void configureSerializers(ObjectMapper objectMapper) {
        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT);
    }

    /*@Bean
    public DeadlineManager deadlineManager(Configuration configuration) {
        return SimpleDeadlineManager.builder().scopeAwareProvider(configuration.scopeAwareProvider()).build();
    }*/

     /*
    @Bean
    public AxonConfiguration configure(ApplicationConfigurer applicationConfigurer) {
         return EventSourcingConfigurer
                .create()
                .messaging(c -> c.registerCommandDispatchInterceptor(b -> new DispatchTimeCommandDispatchInterceptor()))
                 .build();
    }
    */

    /*
    @Autowired
    public void registerCommandInterceptor(Configuration configuration) {
        configuration.commandBus().registerDispatchInterceptor(new DispatchTimeCommandDispatchInterceptor());
    }*/


    @Bean
    public EventStorageEngine storageEngine(AxonServerConnectionManager connectionManager,
                                            EventConverter eventConverter) {
        // AxonServerConnectionManager#getConnection returns a connection to the default context.
        // Use AxonServerConnectionManager#getConnection(String) to retrieve a connection to another context.
        return new AggregateBasedAxonServerEventStorageEngine(
                connectionManager.getConnection(),
                eventConverter
        );
    }

    /*
    @Bean
    public EventProcessorDefinition exampleProcessorDefinition() {
        return EventProcessorDefinition.pooledStreaming("example-processor")
                                       .assigningHandlers(EventHandlerSelector.matchesNamespaceOnType(
                                               "orders"
                                       ))
                                       .customized(config -> config
                                               .initialSegmentCount(4)
                                               .batchSize(100)
                                               .claimExtensionThreshold(5000)
                                               .tokenClaimInterval(5000)
                                       );
    }
     */

    /*
    @Bean
    EventProcessorDefinition orderProcessor() {
        return EventProcessorDefinition.pooledStreaming("order-processor")
                .assigningHandlers(descriptor ->
                        descriptor.beanName().startsWith("order"))
                .customized(config -> config
                        .deadLetterQueue(dlq -> dlq
                                .enabled()
                                .cacheMaxSize(2048)));
    }*/
}
