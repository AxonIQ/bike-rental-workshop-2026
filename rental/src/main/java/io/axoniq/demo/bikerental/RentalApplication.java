package io.axoniq.demo.bikerental;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.axoniq.demo.bikerental.commands.BikeStatus;
import io.axoniq.demo.bikerental.common.DispatchTimeCommandDispatchInterceptor;
import org.axonframework.config.Configuration;
import org.axonframework.deadline.DeadlineManager;
import org.axonframework.deadline.SimpleDeadlineManager;
import org.axonframework.eventhandling.tokenstore.jpa.TokenEntry;
import org.axonframework.modelling.saga.repository.jpa.SagaEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Import(AxonConfig.class)
@EntityScan(basePackageClasses = {BikeStatus.class, SagaEntry.class, TokenEntry.class})
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

    @Bean
    public DeadlineManager deadlineManager(Configuration configuration) {
        return SimpleDeadlineManager.builder().scopeAwareProvider(configuration.scopeAwareProvider()).build();
    }

     /*
    @Bean
    public AxonConfiguration configure(ApplicationConfigurer applicationConfigurer) {
         return EventSourcingConfigurer
                .create()
                .messaging(c -> c.registerCommandDispatchInterceptor(b -> new DispatchTimeCommandDispatchInterceptor()))
                 .build();
    }
    */

    @Autowired
    public void registerCommandInterceptor(Configuration configuration) {
        configuration.commandBus().registerDispatchInterceptor(new DispatchTimeCommandDispatchInterceptor());
    }

    /*
    @Bean
    public EventStorageEngine storageEngine(AxonServerConnectionManager connectionManager,
                                            EventConverter eventConverter) {
        // AxonServerConnectionManager#getConnection returns a connection to the default context.
        // Use AxonServerConnectionManager#getConnection(String) to retrieve a connection to another context.
        return new AggregateBasedAxonServerEventStorageEngine(
                connectionManager.getConnection(),
                eventConverter
        );
    }*/

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
