package com.example.way2rental.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Meta implements Serializable {
    @SerializedName("tags")
    private List<String> tags;
    @SerializedName("facilities")
    private List<String> facilities;
    @SerializedName("rules")
    private List<String> rules;
    @SerializedName("nearby")
    private Map<String, String> nearby; // Changed to Map for dynamic keys like "Mall", "Hospital"
    @SerializedName("attributes")
    private Attributes attributes;

    // Default constructor
    public Meta() {}

    // Getters
    public List<String> getTags() { return tags; }
    public List<String> getFacilities() { return facilities; }
    public List<String> getRules() { return rules; }
    public Map<String, String> getNearby() { return nearby; }
    public Attributes getAttributes() { return attributes; }

    // Setters
    public void setTags(List<String> tags) { this.tags = tags; }
    public void setFacilities(List<String> facilities) { this.facilities = facilities; }
    public void setRules(List<String> rules) { this.rules = rules; }
    public void setNearby(Map<String, String> nearby) { this.nearby = nearby; }
    public void setAttributes(Attributes attributes) { this.attributes = attributes; }
}
