package dev.pott.sucks.api.dto.request.portal;

import com.google.gson.annotations.SerializedName;

public class PortalIotCommandRequest {

    @SerializedName("auth")
    private final PortalAuthRequestParameter auth;

    @SerializedName("cmdName")
    private final String commandName;

    @SerializedName("payload")
    private final String payload;

    @SerializedName("payloadType")
    private final String payloadType;

    @SerializedName("td")
    private final String td = "q";

    @SerializedName("toId")
    private final String targetDeviceId;

    @SerializedName("toRes")
    private final String targetResource;

    @SerializedName("toType")
    private final String targetClass;

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
}
