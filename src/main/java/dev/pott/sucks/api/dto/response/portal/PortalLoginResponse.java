package dev.pott.sucks.api.dto.response.portal;

import com.google.gson.annotations.SerializedName;

public class PortalLoginResponse {

    @SerializedName("todo")
    private final String todo;

    @SerializedName("result")
    private final String result;

    @SerializedName("userId")
    private final String userId;

    @SerializedName("resource")
    private final String resource;

    @SerializedName("token")
    private final String token;

    @SerializedName("last")
    private final String last;

    public PortalLoginResponse(String todo, String result, String userId, String resource, String token, String last) {
        this.todo = todo;
        this.result = result;
        this.userId = userId;
        this.resource = resource;
        this.token = token;
        this.last = last;
    }

    public String getTodo() {
        return todo;
    }

    public String getResult() {
        return result;
    }

    public String getUserId() {
        return userId;
    }

    public String getResource() {
        return resource;
    }

    public String getToken() {
        return token;
    }

    public String getLast() {
        return last;
    }
}
