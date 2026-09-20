package com.monitoring.monitoring_backend.exception;

public class ServerNotFoundException extends RuntimeException {
    public ServerNotFoundException(String message) {
        super(message);
    }
}