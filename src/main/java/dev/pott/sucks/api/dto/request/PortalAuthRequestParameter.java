package dev.pott.sucks.api.dto.request;

import com.google.gson.annotations.SerializedName;

public class PortalAuthRequestParameter {

    @SerializedName("with")
    private final String with;

    @SerializedName("userid")
    private final String userid;

    @SerializedName("realm")
    private final String realm;

    @SerializedName("token")
    private final String token;

    @SerializedName("resource")
    private final String resource;

    public PortalAuthRequestParameter(String with, String userid, String realm, String token, String resource) {
        this.with = with;
        this.userid = userid;
        this.realm = realm;
        this.token = token;
        this.resource = resource;
    }

    public String getWith() {
        return with;
    }

    public String getUserid() {
        return userid;
    }

    public String getRealm() {
        return realm;
    }

    public String getToken() {
        return token;
    }

    public String getResource() {
        return resource;
    }

}
