package com.vriza.donasikita.network.responses;

import com.google.gson.annotations.SerializedName;
import com.vriza.donasikita.network.responses.DonationResponse.DonationDetails;

import java.util.List;

public class DonationHistoryResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<DonationDetails> data;

    // Constructors
    public DonationHistoryResponse() {}

    public DonationHistoryResponse(boolean success, String message, List<DonationDetails> data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DonationDetails> getData() {
        return data;
    }

    public void setData(List<DonationDetails> data) {
        this.data = data;
    }
}