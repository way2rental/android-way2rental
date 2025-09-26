package com.example.way2rental.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable; // Added for Serializable

public class RentalItem implements Serializable { // Added implements Serializable
    @SerializedName("title")
    private String title;

    @SerializedName("price")
    private String price;

    @SerializedName("location")
    private String location;

    @SerializedName("image_url")
    private String imageUrl;

    // Default constructor for Gson
    public RentalItem() {
    }

    // Optional: Constructor for manual creation if needed
    public RentalItem(String title, String price, String location, String imageUrl) {
        this.title = title;
        this.price = price;
        this.location = location;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
