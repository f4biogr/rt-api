package com.example.realtimeapi.controller;

import com.example.realtimeapi.exception.ChannelCreateException;
import com.example.realtimeapi.exception.ChannelDeleteException;
import com.example.realtimeapi.model.ApiResponse;
import com.example.realtimeapi.model.Channel;
import com.rabbitmq.stream.Environment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/channel")
public class ChannelController {

    private final Environment environment;
    private final RabbitAdmin rabbitAdmin;

    @Autowired
    public ChannelController(Environment environment, RabbitAdmin rabbitAdmin) {
        this.environment = environment;
        this.rabbitAdmin = rabbitAdmin;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createChannel(@RequestBody Channel channel) throws ChannelCreateException {
        try {
            environment.streamCreator().stream(channel.getName()).create();
        } catch (Exception ex) {
            throw new ChannelCreateException("Error on create channel");
        }

        return ResponseEntity.ok(new ApiResponse(0, "Channel created successfully"));
    }

    @DeleteMapping("/{channel}")
    public ResponseEntity<ApiResponse> deleteChannel(@PathVariable String channel) throws ChannelDeleteException {
        try {
            environment.deleteStream(channel);
        } catch (Exception ex) {
            throw new ChannelDeleteException("Error on delete channel");
        }

        return ResponseEntity.ok(new ApiResponse(0, "Channel deleted"));
    }

    @GetMapping("/info/{channel}")
    public ResponseEntity<QueueInformation> getChannelInfo(@PathVariable String channel) {
        return ResponseEntity.ok().body(rabbitAdmin.getQueueInfo(channel));
    }
}