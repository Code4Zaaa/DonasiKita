package com.vriza.donasikita.network.responses;

import com.google.gson.annotations.SerializedName;
import com.vriza.donasikita.models.Campaign;

import java.util.List;

public class CampaignResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<Campaign> data;

    @SerializedName("message")
    private String message;

    public List<Campaign> getData() { return data; }
    public void setData(List<Campaign> data) { this.data = data; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isSuccess() {
        return success;
    }

}