package com.example.realtimeapi.exception;

import com.example.realtimeapi.model.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionsHandler {
    
    private static final Logger log = LoggerFactory.getLogger(CustomExceptionsHandler.class);
    
    @ExceptionHandler(ChannelCreateException.class)
    public ResponseEntity<ApiResponse> handleChannelCreateException(ChannelCreateException exception) {
        log.error("Create channel: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(1, "Error on create channel"));
    }

    @ExceptionHandler(ChannelDeleteException.class)
    public ResponseEntity<ApiResponse> handleChannelDeleteException(ChannelDeleteException exception) {
        log.error("Delete channel: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(1, "Error on delete channel"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleException(Exception ex) {
        log.error("Error {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(1, ex.getMessage()));
    }

}
    
