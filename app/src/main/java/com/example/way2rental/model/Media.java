package com.example.way2rental.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Media implements Serializable {
    @SerializedName("images")
    private List<ImageItem> images;
    @SerializedName("videos")
    private List<String> videos; // Assuming video URLs are just strings for now

    // Default constructor
    public Media() {}

    // Getters
    public List<ImageItem> getImages() { return images; }
    public List<String> getVideos() { return videos; }

    // Setters
    public void setImages(List<ImageItem> images) { this.images = images; }
    public void setVideos(List<String> videos) { this.videos = videos; }
}
