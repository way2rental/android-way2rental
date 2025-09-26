package com.example.way2rental.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ReviewItem implements Serializable {
    @SerializedName("userId")
    private int userId;
    @SerializedName("comment")
    private String comment;
    @SerializedName("rating")
    private int rating; // Assuming rating is an int (e.g., 1 to 5)

    // Default constructor
    public ReviewItem() {}

    // Getters
    public int getUserId() { return userId; }
    public String getComment() { return comment; }
    public int getRating() { return rating; }

    // Setters
    public void setUserId(int userId) { this.userId = userId; }
    public void setComment(String comment) { this.comment = comment; }
    public void setRating(int rating) { this.rating = rating; }
}
