package dev.pott.sucks.api.internal.dto.request.portal;

import com.google.gson.annotations.SerializedName;

public class PortalIotCommandRequest {

    @SerializedName("auth")
    final PortalAuthRequestParameter auth;

    @SerializedName("cmdName")
    final String commandName;

    @SerializedName("payload")
    final String payload;

    @SerializedName("payloadType")
    final String payloadType;

    @SerializedName("td")
    final String td = "q";

    @SerializedName("toId")
    final String targetDeviceId;

    @SerializedName("toRes")
    final String targetResource;

    @SerializedName("toType")
    final String targetClass;

    public PortalIotCommandRequest(PortalAuthRequestParameter auth, String commandName, String payload,
            String targetDeviceId, String targetResource, String targetClass, boolean json) {
        this.auth = auth;
        this.commandName = commandName;
        this.payload = payload;
        this.targetDeviceId = targetDeviceId;
        this.targetResource = targetResource;
        this.targetClass = targetClass;
        this.payloadType = json ? "j" : "x";
    }

    public static class JsonPayloadHeader {
        @SerializedName("pri")
        public final int pri = 1;
        @SerializedName("ts")
        public final long timestamp;
        @SerializedName("tzm")
        public final int tzm = 480;
        @SerializedName("ver")
        public final String version = "0.0.50";

        public JsonPayloadHeader() {
            timestamp = System.currentTimeMillis();
        }
    }
}
