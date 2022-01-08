package dev.pott.sucks.api.dto.request.portal;

import com.google.gson.annotations.SerializedName;

import dev.pott.sucks.api.PortalTodo;

public class PortalAuthRequest {

    @SerializedName("todo")
    final PortalTodo todo;

    @SerializedName("userid")
    final String userId;

    @SerializedName("auth")
    final PortalAuthRequestParameter auth;

    public PortalAuthRequest(PortalTodo todo, PortalAuthRequestParameter auth) {
        this.todo = todo;
        this.userId = auth.userId;
        this.auth = auth;
    }
}
