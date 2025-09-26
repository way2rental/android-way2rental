package com.example.way2rental.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Owner implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("contactNumber")
    private String contactNumber;
    @SerializedName("verified")
    private boolean verified;

    // Default constructor
    public Owner() {}

    // Getters
    public int getId() { return id; }
    public String getContactNumber() { return contactNumber; }
    public boolean isVerified() { return verified; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setVerified(boolean verified) { this.verified = verified; }
}
