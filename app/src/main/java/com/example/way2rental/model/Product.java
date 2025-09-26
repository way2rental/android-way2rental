package com.example.way2rental.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name; // This can serve as the 'title'
    @SerializedName("type")
    private String type;
    @SerializedName("status")
    private String status;
    @SerializedName("description")
    private String description;
    @SerializedName("pricing")
    private Pricing pricing;
    @SerializedName("location")
    private ProductLocation location;
    @SerializedName("owner")
    private Owner owner; // To be defined
    @SerializedName("media")
    private Media media;
    @SerializedName("meta")
    private Meta meta; // To be defined
    @SerializedName("reviews")
    private ReviewsSummary reviews; // To be defined
    @SerializedName("availability")
    private Availability availability; // To be defined
    @SerializedName("seo")
    private Seo seo; // To be defined
    @SerializedName("featured")
    private boolean featured;
    @SerializedName("timestamps")
    private Timestamps timestamps; // To be defined

    // Default constructor
    public Product() {}

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public String getDescription() { return description; }
    public Pricing getPricing() { return pricing; }
    public ProductLocation getProductLocation() { return location; } // Renamed getter for clarity
    public Owner getOwner() { return owner; }
    public Media getMedia() { return media; }
    public Meta getMeta() { return meta; }
    public ReviewsSummary getReviews() { return reviews; }
    public Availability getAvailability() { return availability; }
    public Seo getSeo() { return seo; }
    public boolean isFeatured() { return featured; }
    public Timestamps getTimestamps() { return timestamps; }

    // Setters (Optional, but good practice if you might modify them locally)
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setStatus(String status) { this.status = status; }
    public void setDescription(String description) { this.description = description; }
    public void setPricing(Pricing pricing) { this.pricing = pricing; }
    public void setProductLocation(ProductLocation location) { this.location = location; } // Renamed setter
    public void setOwner(Owner owner) { this.owner = owner; }
    public void setMedia(Media media) { this.media = media; }
    public void setMeta(Meta meta) { this.meta = meta; }
    public void setReviews(ReviewsSummary reviews) { this.reviews = reviews; }
    public void setAvailability(Availability availability) { this.availability = availability; }
    public void setSeo(Seo seo) { this.seo = seo; }
    public void setFeatured(boolean featured) { this.featured = featured; }
    public void setTimestamps(Timestamps timestamps) { this.timestamps = timestamps; }

    // Helper method to get primary image URL
    public String getPrimaryImageUrl() {
        if (media != null && media.getImages() != null) {
            for (ImageItem image : media.getImages()) {
                if (image.isPrimary()) {
                    return image.getUrl();
                }
            }
            // Fallback to first image if no primary is marked and list is not empty
            if (!media.getImages().isEmpty()) {
                return media.getImages().get(0).getUrl();
            }
        }
        return null; // Or a placeholder URL
    }

    // Helper to get a simple representation of price for display
    public String getDisplayPrice() {
        if (pricing != null) {
            return "₹" + pricing.getBasePrice() + " " + (pricing.getPriceUnit() != null ? pricing.getPriceUnit().replace("_", " ").toLowerCase() : "/month");
        }
        return "Price not available";
    }

    // Helper to get a simple representation of address for display
    public String getDisplayAddress() {
        if (location != null) {
            return location.getAddressLine1() + ", " + location.getCity();
        }
        return "Location not available";
    }
}
