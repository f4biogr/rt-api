package com.example.realtimeapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    @JsonProperty("return_code")
    private Integer returnCode;

    @JsonProperty("return_message")
    private String returnMessage;
}