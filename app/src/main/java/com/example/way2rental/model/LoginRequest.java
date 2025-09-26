package com.example.way2rental.model;

public class LoginRequest {
    private String authorizationId;
    private String identifier;
    private String otpValue;

    public LoginRequest(String phone, String otp, String authorizationId) {
        this.identifier = phone;
        this.otpValue = otp;
        this.authorizationId = authorizationId;
    }

    public String getAuthorizationId() {
        return authorizationId;
    }

    public void setAuthorizationId(String authorizationId) {
        this.authorizationId = authorizationId;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getOtpValue() {
        return otpValue;
    }

    public void setOtpValue(String otpValue) {
        this.otpValue = otpValue;
    }
}
