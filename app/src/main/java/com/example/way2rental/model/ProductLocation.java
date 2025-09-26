package com.example.way2rental.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ProductLocation implements Serializable {
    @SerializedName("addressLine1")
    private String addressLine1;
    @SerializedName("city")
    private String city;
    @SerializedName("state")
    private String state;
    @SerializedName("country")
    private String country;
    @SerializedName("zipCode")
    private String zipCode;
    @SerializedName("coordinates")
    private Coordinates coordinates;

    // Default constructor
    public ProductLocation() {}

    // Getters
    public String getAddressLine1() { return addressLine1; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getCountry() { return country; }
    public String getZipCode() { return zipCode; }
    public Coordinates getCoordinates() { return coordinates; }

    // Setters
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }
    public void setCity(String city) { this.city = city; }
    public void setState(String state) { this.state = state; }
    public void setCountry(String country) { this.country = country; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }
}
