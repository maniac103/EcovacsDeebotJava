package dev.pott.sucks.cleaner;

import com.google.gson.annotations.SerializedName;

public enum CleanMode {
    @SerializedName("auto") AUTO,
    @SerializedName("border") EDGE,
    @SerializedName("spot") SPOT,
    @SerializedName("SpotArea") SPOT_AREA,
    @SerializedName("singleroom") SINGLE_ROOM,
    @SerializedName("stop") STOP;
}
