package com.vriza.donasikita.models;

import com.google.gson.annotations.SerializedName;

public class Instruction {
    @SerializedName("title")
    private String title;

    @SerializedName("steps")
    private String[] steps;

    public Instruction() {}

    public Instruction(String title, String[] steps) {
        this.title = title;
        this.steps = steps;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String[] getSteps() { return steps; }
    public void setSteps(String[] steps) { this.steps = steps; }
}