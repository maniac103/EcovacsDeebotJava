package dev.pott.sucks.cleaner;

import com.google.gson.annotations.SerializedName;

public enum CleanMode {
    @SerializedName("auto") AUTO,
    @SerializedName("border") EDGE,
    @SerializedName("spot") SPOT,
    @SerializedName(value = "SpotArea", alternate = { "spotArea" }) SPOT_AREA,
    @SerializedName("customArea") CUSTOM_AREA,
    @SerializedName("singleroom") SINGLE_ROOM,
    @SerializedName("pause") PAUSE,
    @SerializedName("stop") STOP,
    @SerializedName(value = "going", alternate = { "goCharging"}) RETURNING,
    @SerializedName("idle") IDLE
}