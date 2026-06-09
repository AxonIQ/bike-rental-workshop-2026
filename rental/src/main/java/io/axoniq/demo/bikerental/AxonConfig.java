package io.axoniq.demo.bikerental;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.axonframework.messaging.correlation.CorrelationDataProvider;
import org.axonframework.messaging.correlation.SimpleCorrelationDataProvider;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AxonConfig {

    @Bean
    public CorrelationDataProvider correlationDataProvider() {
        return new SimpleCorrelationDataProvider("dispatchTime");
    }

    @Bean
    @Qualifier("messageSerializer")
    public Serializer messageSerializer(ObjectMapper objectMapper) {
        return JacksonSerializer.builder()
                .objectMapper(objectMapper)
                .build();
    }

    @Bean
    @Qualifier("eventSerializer")
    public Serializer eventSerializer(ObjectMapper objectMapper) {
        return JacksonSerializer.builder()
                .objectMapper(objectMapper)
                .build();
    }

    @Bean
    @Primary
    @Qualifier("serializer")
    public Serializer serializer(ObjectMapper objectMapper) {
        return JacksonSerializer.builder()
                .objectMapper(objectMapper)
                .build();
    }

}
