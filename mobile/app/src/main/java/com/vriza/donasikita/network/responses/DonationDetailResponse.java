package com.vriza.donasikita.network.responses;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

public class DonationDetailResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private DonationResponse.DonationDetails data;

    @SerializedName("message")
    private String message;

    // Constructors
    public DonationDetailResponse() {}

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public DonationResponse.DonationDetails getData() {
        return data;
    }

    public void setData(DonationResponse.DonationDetails data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}