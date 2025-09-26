package com.example.way2rental.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Coordinates implements Serializable {
    @SerializedName("lat")
    private double lat;
    @SerializedName("lng")
    private double lng;

    // Default constructor
    public Coordinates() {}

    // Getters
    public double getLat() { return lat; }
    public double getLng() { return lng; }

    // Setters
    public void setLat(double lat) { this.lat = lat; }
    public void setLng(double lng) { this.lng = lng; }
}
