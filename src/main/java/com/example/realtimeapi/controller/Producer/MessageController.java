package com.example.realtimeapi.controller;

import com.example.realtimeapi.model.ApiResponse;
import com.example.realtimeapi.model.Message;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/message")
public class MessageController {
    private static final Logger log = LoggerFactory.getLogger(MessageController.class);
    private final Environment environment;

    @Autowired
    public MessageController(Environment environment) {
        this.environment = environment;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createMessage(@RequestBody Message message) {
        try {
            Producer producer = environment.producerBuilder().stream(message.getChannel()).build();
            String payload = String.valueOf(message.getData());
            byte[] encodedMessage = payload.getBytes(StandardCharsets.UTF_8);
            producer.send(producer.messageBuilder().addData(encodedMessage).build(), confirmationStatus -> {
                if (confirmationStatus.isConfirmed()) {
                    log.info("Message received, message offset: {}", confirmationStatus.getMessage().getPublishingId());
                } else {
                    log.error("Message not received in rabbitmq cluster");
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(new ApiResponse(1, "Message created"));
    }
}