package com.example.way2rental.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Attributes implements Serializable {
    @SerializedName("bedrooms")
    private Integer bedrooms;
    @SerializedName("bathrooms")
    private Integer bathrooms;
    @SerializedName("floor")
    private Integer floor;
    @SerializedName("areaSqFt")
    private Integer areaSqFt;

    // Default constructor
    public Attributes() {}

    // Getters
    public Integer getBedrooms() { return bedrooms; }
    public Integer getBathrooms() { return bathrooms; }
    public Integer getFloor() { return floor; }

    // Setters
    public void setBedrooms(Integer bedrooms) { this.bedrooms = bedrooms; }
    public void setBathrooms(Integer bathrooms) { this.bathrooms = bathrooms; }
    public void setFloor(Integer floor) { this.floor = floor; }

    public Integer getAreaSqFt() {
        return areaSqFt;
    }

    public void setAreaSqFt(Integer areaSqFt) {
        this.areaSqFt = areaSqFt;
    }
}
