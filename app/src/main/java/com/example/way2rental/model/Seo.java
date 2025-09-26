package com.example.way2rental.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Seo implements Serializable {
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("slug")
    private String slug;
    @SerializedName("language")
    private String language;

    // Default constructor
    public Seo() {}

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getSlug() { return slug; }
    public String getLanguage() { return language; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setLanguage(String language) { this.language = language; }
}
