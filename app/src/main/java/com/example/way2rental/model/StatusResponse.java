package com.example.way2rental.model;

import com.google.gson.annotations.SerializedName;

public class StatusResponse {

    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("referenceId") // Added this field
    private String referenceId;

    // Getters
    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getReferenceId() {
        return referenceId;
    }
}