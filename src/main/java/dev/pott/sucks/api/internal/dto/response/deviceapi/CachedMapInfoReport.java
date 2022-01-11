package dev.pott.sucks.api.internal.dto.response.deviceapi;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class CachedMapInfoReport {
    @SerializedName("enable")
    public int enable;

    @SerializedName("info")
    public List<CachedMapInfo> mapInfos;

    public static class CachedMapInfo {
        @SerializedName("mid")
        public String mapId;
        public int index;
        public int status;
        @SerializedName("using")
        public int used;
        public int built;
        public String name;
    }
}
