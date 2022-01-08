package dev.pott.sucks.api.dto.request.portal;

import com.google.gson.annotations.SerializedName;

public class PortalAuthRequestParameter {

    @SerializedName("with")
    final String with;

    @SerializedName("userid")
    final String userId;

    @SerializedName("realm")
    final String realm;

    @SerializedName("token")
    final String token;

    @SerializedName("resource")
    final String resource;

    public PortalAuthRequestParameter(String with, String userid, String realm, String token, String resource) {
        this.with = with;
        this.userId = userid;
        this.realm = realm;
        this.token = token;
        this.resource = resource;
    }
}
