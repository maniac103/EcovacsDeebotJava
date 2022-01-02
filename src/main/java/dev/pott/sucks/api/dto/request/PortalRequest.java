package dev.pott.sucks.api.dto.request;

import com.google.gson.annotations.SerializedName;

public class PortalRequest {

    @SerializedName("todo")
    private final String todo;

    @SerializedName("userid")
    private final String userId;

    @SerializedName("auth")
    private final PortalAuthRequestParameter auth;

    public PortalRequest(String todo, String userId, PortalAuthRequestParameter auth) {
        this.todo = todo;
        this.userId = userId;
        this.auth = auth;
    }

    public String getTodo() {
        return todo;
    }

    public String getUserId() {
        return userId;
    }

    public PortalAuthRequestParameter getAuth() {
        return auth;
    }
}
