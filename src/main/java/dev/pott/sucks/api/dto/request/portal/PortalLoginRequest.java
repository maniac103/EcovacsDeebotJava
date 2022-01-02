package dev.pott.sucks.api.dto.request.portal;

import com.google.gson.annotations.SerializedName;

import dev.pott.sucks.api.PortalTodo;

public class PortalLoginRequest {

    @SerializedName("todo")
    private final PortalTodo todo;

    @SerializedName("country")
    private final String country;

    @SerializedName("last")
    private final String last;

    @SerializedName("org")
    private final String org;

    @SerializedName("resource")
    private final String resource;

    @SerializedName("realm")
    private final String realm;

    @SerializedName("token")
    private final String token;

    @SerializedName("userid")
    private final String userId;

    @SerializedName("edition")
    private final String edition;

    public PortalLoginRequest(PortalTodo todo, String country, String last, String org, String resource, String realm, String token, String userId, String edition) {
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

    public PortalTodo getTodo() {
        return todo;
    }

    public String getCountry() {
        return country;
    }

    public String getLast() {
        return last;
    }

    public String getOrg() {
        return org;
    }

    public String getResource() {
        return resource;
    }

    public String getRealm() {
        return realm;
    }

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    public String getEdition() {
        return edition;
    }
}
