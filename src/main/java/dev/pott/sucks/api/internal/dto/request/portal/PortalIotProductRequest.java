package dev.pott.sucks.api.internal.dto.request.portal;

import com.google.gson.annotations.SerializedName;

public class PortalIotProductRequest {

    @SerializedName("todo")
    final String todo = "";

    @SerializedName("channel")
    final String channel = "";

    @SerializedName("auth")
    final PortalAuthRequestParameter auth;

    public PortalIotProductRequest(PortalAuthRequestParameter auth) {
        this.auth = auth;
    }
}
