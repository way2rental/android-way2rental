package com.example.way2rental.model;

import com.google.gson.annotations.SerializedName; // Added this import

public class LoginResponse {
    private String tokenType;
    private String token;
    private String refreshToken;
    private Long expiresIn;
    private String userName;
    private String userType;
    private String appCode;

    @SerializedName("firstLogin") // Assuming JSON key is "firstLogin" for this field too
    private boolean isFirstLogin;

    @SerializedName("passwordChangeRequired") // Assuming JSON key for this
    private boolean passwordChangeRequired;

    @SerializedName("newRegistration") // This is the key change
    private boolean isNewRegistration;

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public boolean isFirstLogin() {
        return isFirstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        isFirstLogin = firstLogin;
    }

    public boolean isPasswordChangeRequired() {
        return passwordChangeRequired;
    }

    public void setPasswordChangeRequired(boolean passwordChangeRequired) {
        this.passwordChangeRequired = passwordChangeRequired;
    }

    public boolean isNewRegistration() {
        return isNewRegistration;
    }

    public void setNewRegistration(boolean newRegistration) {
        isNewRegistration = newRegistration;
    }
}
