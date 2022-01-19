package dev.pott.sucks.cleaner;

import java.util.Date;

public class CleanLogRecord {
    public final Date timestamp;
    public final long cleaningDuration;
    public final int cleanedArea;
    public final byte[] mapImagePngData;
    public final CleanMode mode;

    public CleanLogRecord(long timestamp, long duration, int area, byte[] mapData, CleanMode mode) {
        this.timestamp = new Date(timestamp * 1000);
        this.cleaningDuration = duration;
        this.cleanedArea = area;
        this.mapImagePngData = mapData;
        this.mode = mode;
    }
}
