package com.example.way2rental.model;

import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("name")
    private String name;

    @SerializedName("image_url") // Changed from "icon_resource_name"
    private String imageUrl;    // Changed from iconResourceName

    // Default constructor for Gson
    public Category() {
    }

    // Optional: Constructor for manual creation if needed
    public Category(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl; // Changed
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() { // Getter changed
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) { // Setter changed
        this.imageUrl = imageUrl;
    }
}
