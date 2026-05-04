package io.axoniq.demo.bikerental.common;

import org.axonframework.messaging.commandhandling.CommandMessage;
import org.axonframework.messaging.core.MessageDispatchInterceptor;
import org.axonframework.messaging.core.MessageDispatchInterceptorChain;
import org.axonframework.messaging.core.MessageStream;
import org.axonframework.messaging.core.unitofwork.ProcessingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.Collections;

/**
 * Command dispatch interceptor that logs information about dispatched commands.
 * This interceptor is invoked before commands are dispatched to the command bus.
 */
public class DispatchTimeCommandDispatchInterceptor implements MessageDispatchInterceptor<CommandMessage> {

    private static final Logger logger = LoggerFactory.getLogger(DispatchTimeCommandDispatchInterceptor.class);


    @Override
    public MessageStream<?> interceptOnDispatch(CommandMessage message,
                                                @Nullable ProcessingContext context,
                                                MessageDispatchInterceptorChain<CommandMessage> chain) {
        // Modify or enrich message
        CommandMessage enrichedMessage = message.andMetadata(
                Collections.singletonMap("dispatchTime", Instant.now().toString())
        );

        // Continue chain with modified message
        return chain.proceed(enrichedMessage, context);
    }

    /*@Override
    public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
            List<? extends CommandMessage<?>> messages) {


        return (index, command) -> {
            // Return the command unmodified (you could modify it here if needed)
            return command.withMetaData(Map.of("dispatchTime", Instant.now().toString()));
        };
    }*/
}