package com.example.way2rental.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Pricing implements Serializable {
    @SerializedName("basePrice")
    private double basePrice;
    @SerializedName("securityDeposit")
    private double securityDeposit;
    @SerializedName("priceUnit")
    private String priceUnit; // e.g., "PER_MONTH"
    @SerializedName("negotiable")
    private boolean negotiable;
    @SerializedName("cleaningFee")
    private double cleaningFee;
    @SerializedName("maintenanceFee")
    private double maintenanceFee;
    @SerializedName("discountPercent")
    private double discountPercent;

    // Default constructor
    public Pricing() {}

    // Getters
    public double getBasePrice() { return basePrice; }
    public double getSecurityDeposit() { return securityDeposit; }
    public String getPriceUnit() { return priceUnit; }
    public boolean isNegotiable() { return negotiable; }
    public double getCleaningFee() { return cleaningFee; }
    public double getMaintenanceFee() { return maintenanceFee; }
    public double getDiscountPercent() { return discountPercent; }

    // Setters
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    public void setSecurityDeposit(double securityDeposit) { this.securityDeposit = securityDeposit; }
    public void setPriceUnit(String priceUnit) { this.priceUnit = priceUnit; }
    public void setNegotiable(boolean negotiable) { this.negotiable = negotiable; }
    public void setCleaningFee(double cleaningFee) { this.cleaningFee = cleaningFee; }
    public void setMaintenanceFee(double maintenanceFee) { this.maintenanceFee = maintenanceFee; }
    public void setDiscountPercent(double discountPercent) { this.discountPercent = discountPercent; }
}
