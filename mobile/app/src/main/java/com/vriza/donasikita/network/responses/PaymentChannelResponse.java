package com.vriza.donasikita.network.responses;

import com.google.gson.annotations.SerializedName;
import com.vriza.donasikita.models.PaymentChannel;
import java.util.List;

public class PaymentChannelResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<PaymentChannel> data;

    // Constructors
    public PaymentChannelResponse() {}

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<PaymentChannel> getData() { return data; }
    public void setData(List<PaymentChannel> data) { this.data = data; }
}