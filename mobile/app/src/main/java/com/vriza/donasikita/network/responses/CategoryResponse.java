package com.vriza.donasikita.network.responses;

import com.google.gson.annotations.SerializedName;
import com.vriza.donasikita.models.Category;

import java.util.List;

public class CategoryResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<Category> data;

    // Constructors
    public CategoryResponse() {}

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public List<Category> getData() { return data; }
    public void setData(List<Category> data) { this.data = data; }
}
