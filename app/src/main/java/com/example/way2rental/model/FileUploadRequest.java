package com.example.way2rental.model;

import com.google.gson.annotations.SerializedName;

public class FileUploadRequest {

    @SerializedName("fileName")
    private String fileName;

    @SerializedName("fileType")
    private String fileType; // This typically refers to the MIME type on the client

    @SerializedName("fileSize")
    private String fileSize; // Consider long if it's in bytes

    @SerializedName("filePath")
    private String filePath; // This might be redundant if the file is sent as Multipart,
                             // but included as per your model. Backend might use it for storage hints.

    @SerializedName("provideType")
    private ProviderType provideType = ProviderType.DB; // Default as per your model

    @SerializedName("description")
    private String description;

    // Constructors (optional, but can be helpful)
    public FileUploadRequest() {
    }

    public FileUploadRequest(String fileName, String fileType, String fileSize, ProviderType provideType) {
        this.fileName = fileName;
        this.fileType = fileType; // e.g., "image/jpeg"
        this.fileSize = fileSize; // e.g., "1024768" (bytes as string)
        this.provideType = provideType;
    }


    // Getters and Setters
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public ProviderType getProvideType() {
        return provideType;
    }

    public void setProvideType(ProviderType provideType) {
        this.provideType = provideType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}