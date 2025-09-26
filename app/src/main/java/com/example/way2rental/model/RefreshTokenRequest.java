package com.example.way2rental.model;

import com.google.gson.annotations.SerializedName;

public class RefreshTokenRequest {

    @SerializedName("device_id")
    private String deviceId; // To be populated with Settings.Secure.ANDROID_ID

    @SerializedName("refresh_token")
    private String refreshToken; // The actual refresh token string

    @SerializedName("token_type")
    private String tokenType; // e.g., "BEARER", retrieved from initial login/saved prefs

    @SerializedName("identifier")
    private String identifier; // e.g., phone number, retrieved from initial login/saved prefs

    @SerializedName("app_code")
    private String appCode; // Your application's specific code, retrieved from initial login/saved prefs

    public RefreshTokenRequest(String deviceId, String refreshToken, String tokenType, String identifier, String appCode) {
        this.deviceId = deviceId;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.identifier = identifier;
        this.appCode = appCode;
    }

    // Getters are not strictly necessary if this class is only used for sending data with Gson,
    // but can be added for completeness or testing if desired.
    // public String getDeviceId() { return deviceId; }
    // public String getRefreshToken() { return refreshToken; }
    // public String getTokenType() { return tokenType; }
    // public String getIdentifier() { return identifier; }
    // public String getAppCode() { return appCode; }
}
