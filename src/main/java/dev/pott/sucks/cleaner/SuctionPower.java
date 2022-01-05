package dev.pott.sucks.cleaner;

import com.google.gson.annotations.SerializedName;

public enum SuctionPower {
    @SerializedName("standard")
    NORMAL,
    @SerializedName("strong")
    HIGH,
    HIGHER,
    SILENT;

    public static SuctionPower fromJsonValue(int value) {
        switch (value) {
            case 1000:
                return SILENT;
            case 1:
                return HIGH;
            case 2:
                return HIGHER;
            default:
                return NORMAL;
        }
    }
}
