package dev.pott.sucks.api.internal.dto.response.deviceapi;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import dev.pott.sucks.cleaner.CleanMode;

public class CleanReport {
    @SerializedName("trigger")
    public String trigger; // app, workComplete, ...?
    @SerializedName("state")
    public String state;
    @SerializedName("cleanState")
    public CleanStateReport cleanState;

    public static class CleanStateReport {
        @SerializedName("router")
        public String router; // plan, ...?
        @SerializedName("type")
        public String type;
        @SerializedName("motionState")
        public String motionState;
    }

    public CleanMode determineCleanMode(Gson gson) {
        final String modeValue;
        if (cleanState != null) {
            if ("working".equals(cleanState.motionState)) {
                modeValue = cleanState.type;
            } else {
                modeValue = cleanState.motionState;
            }
        } else {
            modeValue = state;
        }
        return gson.fromJson(modeValue, CleanMode.class);
    }
}
