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

    public int toJsonValue() {
        switch (this) {
            case HIGH:
                return 1;
            case HIGHER:
                return 2;
            case SILENT:
                return 1000;
            default: // NORMAL
                return 0;
        }
    }

    public String toXmlValue() {
        if (this == HIGH) {
            return "strong";
        }
        return "standard";
    }
}
