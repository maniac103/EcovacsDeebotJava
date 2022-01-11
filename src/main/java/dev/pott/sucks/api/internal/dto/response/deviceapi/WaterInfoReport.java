package dev.pott.sucks.api.internal.dto.response.deviceapi;

import com.google.gson.annotations.SerializedName;

public class WaterInfoReport {
    @SerializedName("enable")
    public int waterPlatePresent;
    @SerializedName("amount")
    public int waterAmount;
}
