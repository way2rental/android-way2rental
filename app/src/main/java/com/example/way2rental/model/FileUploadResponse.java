package com.example.way2rental.model;

import com.google.gson.annotations.SerializedName;

public class FileUploadResponse {

    @SerializedName("fileId") // Example field, adjust to your actual API response
    private String fileId;

    @SerializedName("url") // Example field, if the API returns a direct URL
    private String url;

    @SerializedName("fileName")
    private String fileName;

    @SerializedName("message")
    private String message;

    // --- Getters ---
    public String getFileId() {
        return fileId;
    }

    public String getUrl() {
        return url;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMessage() {
        return message;
    }
}