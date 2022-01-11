package dev.pott.sucks.api.internal.dto.response.deviceapi;

import com.google.gson.annotations.SerializedName;

public class BatteryReport {
    @SerializedName("value")
    public int percent;
    @SerializedName("isLow")
    public int batteryIsLow;
}
