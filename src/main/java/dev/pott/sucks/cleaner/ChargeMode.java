package dev.pott.sucks.cleaner;

import com.google.gson.annotations.SerializedName;

public enum ChargeMode {
    @SerializedName("go")
    RETURN,
    @SerializedName("Going")
    RETURNING,
    @SerializedName("SlotCharging")
    CHARGING,
    @SerializedName("Idle")
    IDLE;
}
