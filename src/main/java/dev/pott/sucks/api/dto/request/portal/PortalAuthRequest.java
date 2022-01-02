package dev.pott.sucks.api.dto.request.portal;

import com.google.gson.annotations.SerializedName;

import dev.pott.sucks.api.PortalTodo;

public class PortalAuthRequest {

    @SerializedName("todo")
    private final PortalTodo todo;

    @SerializedName("userid")
    private final String userId;

    @SerializedName("auth")
    private final PortalAuthRequestParameter auth;

    public PortalAuthRequest(PortalTodo todo, String userId, PortalAuthRequestParameter auth) {
        this.todo = todo;
        this.userId = userId;
        this.auth = auth;
    }

    public PortalTodo getTodo() {
        return todo;
    }

    public String getUserId() {
        return userId;
    }

    public PortalAuthRequestParameter getAuth() {
        return auth;
    }
}
