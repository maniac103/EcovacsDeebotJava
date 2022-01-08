package dev.pott.sucks.api.dto.request.portal;

import com.google.gson.annotations.SerializedName;

import dev.pott.sucks.api.PortalTodo;

public class PortalLoginRequest {

    @SerializedName("todo")
    final PortalTodo todo;

    @SerializedName("country")
    final String country;

    @SerializedName("last")
    final String last;

    @SerializedName("org")
    final String org;

    @SerializedName("resource")
    final String resource;

    @SerializedName("realm")
    final String realm;

    @SerializedName("token")
    final String token;

    @SerializedName("userid")
    final String userId;

    @SerializedName("edition")
    final String edition;

    public PortalLoginRequest(PortalTodo todo, String country, String last, String org, String resource, String realm,
            String token, String userId, String edition) {
        this.todo = todo;
        this.country = country;
        this.last = last;
        this.org = org;
        this.resource = resource;
        this.realm = realm;
        this.token = token;
        this.userId = userId;
        this.edition = edition;
    }
}
