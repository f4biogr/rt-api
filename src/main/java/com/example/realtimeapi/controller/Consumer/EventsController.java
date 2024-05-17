package com.example.realtimeapi.controller.Consumer;

import com.rabbitmq.stream.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/events")
@Slf4j
public class EventsController {
    private final Environment environment;
    private final ExecutorService nonBlockingService = Executors.newCachedThreadPool();

    public EventsController(Environment environment) {
        this.environment = environment;
    }

    private static void handle(MessageHandler.Context context, Message message, SseEmitter emitter) {
        SseEmitter.SseEventBuilder event = SseEmitter.event()
        .data(message.getBody().toString())
        .id(String.valueOf(context.offset()))
        .reconnectTime(5000);
        try {
            emitter.send(event);
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }

    @GetMapping
    public SseEmitter getEvents(@RequestParam String ch){
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        
        nonBlockingService.execute(() -> environment.consumerBuilder()
        .stream(ch)
        .offset(OffsetSpecification.first())
        .messageHandler(((context, message) -> handle(context, message, emitter)))
        .autoTrackingStrategy()
        .messageCountBeforeStorage(5)
        .build()
        );
        
        return emitter;
    }
}