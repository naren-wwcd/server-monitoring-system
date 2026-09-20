package com.monitoring.monitoring_backend.exception;

public class MetricNotFoundException extends RuntimeException{

    public MetricNotFoundException(String message)
    {
        super(message);
    }

}
