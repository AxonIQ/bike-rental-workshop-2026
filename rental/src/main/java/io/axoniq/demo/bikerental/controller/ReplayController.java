package io.axoniq.demo.bikerental.controller;

import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/replay")
public class ReplayController {

    private final EventProcessingConfiguration eventProcessingConfiguration;

    public ReplayController(EventProcessingConfiguration eventProcessingConfiguration) {
        this.eventProcessingConfiguration = eventProcessingConfiguration;
    }

    /**
     * Trigger a replay for a specific processor by resetting its tokens and starting it again.
     *
     * @param processorName the name of the processor to replay
     * @return ResponseEntity with status message
     */
    @PostMapping("/{processorName}")
    public ResponseEntity<Map<String, String>> triggerReplay(@PathVariable String processorName) {
        Map<String, String> response = new HashMap<>();

        Optional<TrackingEventProcessor> processor = eventProcessingConfiguration
                .eventProcessor(processorName, TrackingEventProcessor.class);

        if (processor.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Processor '" + processorName + "' not found or is not a TrackingEventProcessor");
            return ResponseEntity.badRequest().body(response);
        }

        TrackingEventProcessor trackingProcessor = processor.get();

        // Shutdown the processor
        trackingProcessor.shutDown();

        // Reset the tokens to replay from the beginning
        trackingProcessor.resetTokens();

        // Start the processor again
        trackingProcessor.start();

        response.put("status", "success");
        response.put("message", "Replay triggered for processor: " + processorName);
        return ResponseEntity.ok(response);
    }

    /**
     * Get the status of a specific processor.
     *
     * @param processorName the name of the processor
     * @return ResponseEntity with processor status
     */
    @GetMapping("/{processorName}/status")
    public ResponseEntity<Map<String, Object>> getProcessorStatus(@PathVariable String processorName) {
        Map<String, Object> response = new HashMap<>();

        Optional<TrackingEventProcessor> processor = eventProcessingConfiguration
                .eventProcessor(processorName, TrackingEventProcessor.class);

        if (processor.isEmpty()) {
            response.put("status", "error");
            response.put("message", "Processor '" + processorName + "' not found or is not a TrackingEventProcessor");
            return ResponseEntity.badRequest().body(response);
        }

        TrackingEventProcessor trackingProcessor = processor.get();

        response.put("processorName", processorName);
        response.put("isRunning", trackingProcessor.isRunning());
        response.put("isError", trackingProcessor.isError());

        return ResponseEntity.ok(response);
    }

    /**
     * List all available event processors.
     *
     * @return ResponseEntity with list of processor names
     */
    @GetMapping("/processors")
    public ResponseEntity<Map<String, Object>> listProcessors() {
        Map<String, Object> response = new HashMap<>();
        response.put("processors", eventProcessingConfiguration.eventProcessors().keySet());
        return ResponseEntity.ok(response);
    }
}