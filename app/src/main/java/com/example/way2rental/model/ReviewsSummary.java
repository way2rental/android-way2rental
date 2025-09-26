package com.example.way2rental.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class ReviewsSummary implements Serializable {
    @SerializedName("avgRating")
    private double avgRating;
    @SerializedName("totalReviews")
    private int totalReviews;
    @SerializedName("list")
    private List<ReviewItem> list;

    // Default constructor
    public ReviewsSummary() {}

    // Getters
    public double getAvgRating() { return avgRating; }
    public int getTotalReviews() { return totalReviews; }
    public List<ReviewItem> getList() { return list; }

    // Setters
    public void setAvgRating(double avgRating) { this.avgRating = avgRating; }
    public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }
    public void setList(List<ReviewItem> list) { this.list = list; }
}
