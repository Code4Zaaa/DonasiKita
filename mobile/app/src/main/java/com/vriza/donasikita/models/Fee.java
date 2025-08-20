package com.vriza.donasikita.models;

import com.google.gson.annotations.SerializedName;

public class Fee {
    @SerializedName("flat")
    private int flat;

    @SerializedName("percent")
    private double percent;

    public Fee() {}

    public int getFlat() { return flat; }
    public void setFlat(int flat) { this.flat = flat; }

    public double getPercent() { return percent; }
    public void setPercent(double percent) { this.percent = percent; }
}