package com.vriza.donasikita.network.responses;

import com.vriza.donasikita.models.User;
import com.google.gson.annotations.SerializedName;


public class UserResponse {

    @SerializedName("success")
    private boolean success;


    @SerializedName("message")
    private String message;

    @SerializedName("user")
    private User user;

    @SerializedName("api_token")
    private String apiToken;

    // Getters
    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public String getApiToken() {
        return apiToken;
    }

    // Setters
    public void setMessage(String message) {
        this.message = message;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }
    public boolean isSuccess() {
        return success;
    }
}