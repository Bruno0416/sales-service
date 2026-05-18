package com.mariluz.sales.dto;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ErrorResponse {

    private LocalDateTime timeStamp;

    private Integer status;

    private String message;

    private Map<String, String> errors;

    private String endpoint;
}
