package dev.pott.sucks.cleaner;

public enum MoppingWaterAmount {
    LOW,
    MEDIUM,
    HIGH,
    VERY_HIGH;

    public static MoppingWaterAmount fromApiValue(int value) {
        return MoppingWaterAmount.values()[value - 1];
    }
}
