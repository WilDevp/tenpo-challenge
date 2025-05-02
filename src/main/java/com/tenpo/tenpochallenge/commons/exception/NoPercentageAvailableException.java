package com.tenpo.tenpochallenge.commons.exception;

/**
 * Exception thrown when no percentage is available (neither from external service nor from cache)
 */
public class NoPercentageAvailableException extends RuntimeException {
    
    public NoPercentageAvailableException(String message) {
        super(message);
    }
    
    public NoPercentageAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
