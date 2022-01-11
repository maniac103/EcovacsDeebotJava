package dev.pott.sucks.cleaner;

import com.google.gson.annotations.SerializedName;

public enum DeviceCapability {
    @SerializedName("mopping_system")
    MOPPING_SYSTEM,
    @SerializedName("main_brush")
    MAIN_BRUSH,
    @SerializedName("voice_reporting")
    VOICE_REPORTING,
    @SerializedName("spot_area_cleaning")
    SPOT_AREA_CLEANING,
    @SerializedName("clean_speed_control")
    CLEAN_SPEED_CONTROL
}
