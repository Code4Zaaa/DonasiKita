package com.vriza.donasikita.models;

import com.google.gson.annotations.SerializedName;

public class PaymentChannel {
    @SerializedName("group")
    private String group;

    @SerializedName("code")
    private String code;

    @SerializedName("name")
    private String name;

    @SerializedName("type")
    private String type;

    @SerializedName("fee_merchant")
    private Fee feeMerchant;

    @SerializedName("fee_customer")
    private Fee feeCustomer;

    @SerializedName("total_fee")
    private Fee totalFee;

    @SerializedName("minimum_fee")
    private Integer minimumFee;

    @SerializedName("maximum_fee")
    private Integer maximumFee;

    @SerializedName("minimum_amount")
    private Integer minimumAmount;

    @SerializedName("maximum_amount")
    private Integer maximumAmount;

    @SerializedName("icon_url")
    private String iconUrl;

    @SerializedName("active")
    private boolean active;

    // Constructors
    public PaymentChannel() {}

    // Getters and Setters
    public String getGroup() { return group; }
    public void setGroup(String group) { this.group = group; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Fee getFeeMerchant() { return feeMerchant; }
    public void setFeeMerchant(Fee feeMerchant) { this.feeMerchant = feeMerchant; }

    public Fee getFeeCustomer() { return feeCustomer; }
    public void setFeeCustomer(Fee feeCustomer) { this.feeCustomer = feeCustomer; }

    public Fee getTotalFee() { return totalFee; }
    public void setTotalFee(Fee totalFee) { this.totalFee = totalFee; }

    public Integer getMinimumFee() { return minimumFee; }
    public void setMinimumFee(Integer minimumFee) { this.minimumFee = minimumFee; }

    public Integer getMaximumFee() { return maximumFee; }
    public void setMaximumFee(Integer maximumFee) { this.maximumFee = maximumFee; }

    public Integer getMinimumAmount() { return minimumAmount; }
    public void setMinimumAmount(Integer minimumAmount) { this.minimumAmount = minimumAmount; }

    public Integer getMaximumAmount() { return maximumAmount; }
    public void setMaximumAmount(Integer maximumAmount) { this.maximumAmount = maximumAmount; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
