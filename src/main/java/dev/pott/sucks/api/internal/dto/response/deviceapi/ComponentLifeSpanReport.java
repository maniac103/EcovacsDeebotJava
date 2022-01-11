package dev.pott.sucks.api.internal.dto.response.deviceapi;

import com.google.gson.annotations.SerializedName;

public class ComponentLifeSpanReport {
    @SerializedName("type")
    public String type;

    @SerializedName("left")
    public int left;

    @SerializedName("total")
    public int total;
}
