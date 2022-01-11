package dev.pott.sucks.api.internal.dto.response.deviceapi;

import com.google.gson.annotations.SerializedName;

public class StatsReport {
    @SerializedName("area")
    public int area;
    @SerializedName("time")
    public int timeInSeconds;
    @SerializedName("cid")
    public String cid; // run ID
    @SerializedName("start")
    public long startTimestamp;
    @SerializedName("type")
    public String type; // auto, ... ?
}
