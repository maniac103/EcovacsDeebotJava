package dev.pott.sucks.api.internal.dto.response.deviceapi;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MapSetReport {
    public String type;
    public int count;
    @SerializedName("mid")
    public String mapId;
    @SerializedName("msid")
    public String mapSetId;
    public List<MapSubSetInfo> subsets;

    public static class MapSubSetInfo {
        @SerializedName("mssid")
        public String id;
    }
}
