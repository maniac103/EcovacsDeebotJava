package dev.pott.sucks.api.internal.dto.response.portal;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import dev.pott.sucks.cleaner.CleanMode;

public class PortalCleanLogsResponse {
    public static class LogRecord {
        @SerializedName("ts")
        public final long timestamp;

        @SerializedName("last")
        public final long duration;

        public final int area;

        public final String id;

        public final String imageUrl;

        public final CleanMode type;

        // TODO: aiavoid (int), aitypes (list of something), stopReason (int)

        LogRecord(long timestamp, long duration, int area, String id, String imageUrl, CleanMode type) {
            this.timestamp = timestamp;
            this.duration = duration;
            this.area = area;
            this.id = id;
            this.imageUrl = imageUrl;
            this.type = type;
        }
    }

    @SerializedName("logs")
    public final List<LogRecord> records;

    @SerializedName("ret")
    final String result;

    PortalCleanLogsResponse(String result, List<LogRecord> records) {
        this.result = result;
        this.records = records;
    }

    public boolean wasSuccessful() {
        return "ok".equals(result);
    }
}
