package io.axoniq.demo.bikerental;

import org.axonframework.messaging.correlation.CorrelationDataProvider;
import org.axonframework.messaging.correlation.SimpleCorrelationDataProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfig {

    @Bean
    public CorrelationDataProvider correlationDataProvider() {
        return new SimpleCorrelationDataProvider("dispatchTime");
    }
}
