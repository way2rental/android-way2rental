package com.example.way2rental.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Availability implements Serializable {
    @SerializedName("from")
    private String from; // Keep as String, can be parsed to Date if needed
    @SerializedName("to")
    private String to;   // Keep as String, can be parsed to Date if needed

    // Default constructor
    public Availability() {}

    // Getters
    public String getFrom() { return from; }
    public String getTo() { return to; }

    // Setters
    public void setFrom(String from) { this.from = from; }
    public void setTo(String to) { this.to = to; }
}
