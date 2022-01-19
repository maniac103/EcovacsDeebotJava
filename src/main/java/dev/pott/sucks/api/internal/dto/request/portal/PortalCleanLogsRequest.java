package dev.pott.sucks.api.internal.dto.request.portal;

import com.google.gson.annotations.SerializedName;

public class PortalCleanLogsRequest {

    @SerializedName("auth")
    final PortalAuthRequestParameter auth;

    @SerializedName("td")
    final String commandName = "GetCleanLogs";

    @SerializedName("did")
    final String targetDeviceId;

    @SerializedName("resource")
    final String targetResource;

    public PortalCleanLogsRequest(PortalAuthRequestParameter auth, String targetDeviceId, String targetResource) {
        this.auth = auth;
        this.targetDeviceId = targetDeviceId;
        this.targetResource = targetResource;
    }
}
