package com.tenpo.tenpochallenge.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Domain model for call records
 * Uses JsonRawValue to present nested JSON content without escape characters
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallRecord {
    private Long id;
    private String endpoint;
    
    @JsonRawValue
    private String requestParams;
    
    @JsonRawValue
    private String response;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorMessage;
    
    private Integer statusCode;
    private boolean isError;
    private LocalDateTime timestamp;
    
    @Override
    public String toString() {
        return "CallRecord{" +
                "endpoint='" + endpoint + '\'' +
                ", requestParams='" + requestParams + '\'' +
                ", response='" + response + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", statusCode=" + statusCode +
                ", isError=" + isError +
                ", timestamp=" + timestamp +
                '}';
    }
}