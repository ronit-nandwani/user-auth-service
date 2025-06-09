package org.example.userauthservice.exceptions;

public class InvalidTokenException extends RuntimeException {
    private String message;

    public InvalidTokenException(String message) {
        super(message);
    }
}
