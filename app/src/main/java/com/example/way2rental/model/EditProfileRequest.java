package com.example.way2rental.model;

import com.google.gson.annotations.SerializedName;

public class EditProfileRequest {

    // Identifier is now a path parameter, so it's removed from the request body.

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("alternatePhone")
    private String alternatePhone;

    @SerializedName("address")
    private String address;

    @SerializedName("city")
    private String city;

    @SerializedName("state")
    private String state;

    @SerializedName("country")
    private String country;

    @SerializedName("zipCode")
    private String zipCode;

    // Constructor without identifier
    public EditProfileRequest(String name, String email, String alternatePhone,
                              String address, String city, String state, String country, String zipCode) {
        this.name = name;
        this.email = email;
        this.alternatePhone = alternatePhone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zipCode = zipCode;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAlternatePhone() {
        return alternatePhone;
    }

    public void setAlternatePhone(String alternatePhone) {
        this.alternatePhone = alternatePhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
