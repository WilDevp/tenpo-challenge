package com.tenpo.tenpochallenge.application.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for a call record
 */
@Data
public class CallRecordDTO {
    private LocalDateTime timestamp;
    private String endpoint;
    private String parameters;
    private String response;
    private boolean isError;
}
