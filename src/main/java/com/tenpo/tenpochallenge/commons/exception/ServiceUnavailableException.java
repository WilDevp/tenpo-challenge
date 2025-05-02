package com.tenpo.tenpochallenge.commons.exception;

/**
 * Exception thrown when an external service is not available
 */
public class ServiceUnavailableException extends RuntimeException {
    
    public ServiceUnavailableException(String message) {
        super(message);
    }
    
    public ServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
