package dev.pott.sucks.api.dto.request.portal;

import com.google.gson.annotations.SerializedName;

public class PortalIotProductRequest {

    @SerializedName("todo")
    private final String todo = "";

    @SerializedName("channel")
    private final String channel = "";

    @SerializedName("auth")
    private final PortalAuthRequestParameter auth;

    public PortalIotProductRequest(PortalAuthRequestParameter auth) {
        this.auth = auth;
    }
}
