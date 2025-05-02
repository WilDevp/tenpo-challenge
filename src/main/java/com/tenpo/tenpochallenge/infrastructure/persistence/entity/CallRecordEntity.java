package com.tenpo.tenpochallenge.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA entity to store call logs in the database
 */
@Entity
@Table(name = "call_records")
public class CallRecordEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @Column(nullable = false, length = 100)
    private String endpoint;
    
    @Column(name = "request_params", length = 1000)
    private String requestParams;
    
    @Column(length = 1000)
    private String response;
    
    @Column(name = "error_message", length = 1000)
    private String errorMessage;
    
    @Column(name = "status_code")
    private int statusCode;
    
    public CallRecordEntity() {
    }
    
    public CallRecordEntity(LocalDateTime timestamp, String endpoint, String requestParams, 
                           String response, String errorMessage, int statusCode) {
        this.timestamp = timestamp;
        this.endpoint = endpoint;
        this.requestParams = requestParams;
        this.response = response;
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getRequestParams() {
        return requestParams;
    }

    public String getResponse() {
        return response;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
