package com.mariluz.sales.dto;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorResponse {

    private LocalDateTime timeStamp;

    private Integer status;

    private String message;

    private Map<String, String> errors;

    private String endpoint;
}
