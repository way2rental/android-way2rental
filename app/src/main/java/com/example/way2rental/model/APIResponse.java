package com.example.way2rental.model;

import java.time.LocalDateTime;
import java.util.List;

public class APIResponse<T> {
    private String correlationId;

//    pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private List<Object> errorDetails;

    // Data Section
    private T data;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Object> getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(List<Object> errorDetails) {
        this.errorDetails = errorDetails;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
