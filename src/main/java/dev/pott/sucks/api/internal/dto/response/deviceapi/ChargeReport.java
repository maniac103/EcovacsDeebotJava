package dev.pott.sucks.api.internal.dto.response.deviceapi;

import com.google.gson.annotations.SerializedName;

public class ChargeReport {
    @SerializedName("isCharging")
    public int isCharging;
    @SerializedName("mode")
    public String mode; // slot, ...?
}
