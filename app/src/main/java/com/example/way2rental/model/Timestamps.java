package com.example.way2rental.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Timestamps implements Serializable {
    @SerializedName("createdAt")
    private String createdAt; // Keep as String, can be parsed to Date if needed
    @SerializedName("updatedAt")
    private String updatedAt; // Keep as String, can be parsed to Date if needed

    // Default constructor
    public Timestamps() {}

    // Getters
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }

    // Setters
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
