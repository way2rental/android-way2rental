package com.example.way2rental.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ImageItem implements Serializable {
    @SerializedName("url")
    private String url;
    @SerializedName("isPrimary")
    private boolean isPrimary;

    // Default constructor
    public ImageItem() {}

    // Getters
    public String getUrl() { return url; }
    public boolean isPrimary() { return isPrimary; }

    // Setters
    public void setUrl(String url) { this.url = url; }
    public void setPrimary(boolean primary) { isPrimary = primary; }
}
