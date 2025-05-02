package com.tenpo.tenpochallenge.commons.exception;

/**
 * Exception thrown when input data is invalid
 */
public class InvalidInputException extends RuntimeException {
    
    public InvalidInputException(String message) {
        super(message);
    }
    
    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
