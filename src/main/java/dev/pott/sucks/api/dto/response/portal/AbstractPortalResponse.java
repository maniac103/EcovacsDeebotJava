package dev.pott.sucks.api.dto.response.portal;

import com.google.gson.annotations.SerializedName;

public abstract class AbstractPortalResponse {

    @SerializedName("todo")
    private final String todo;

    @SerializedName("result")
    private final String result;

    protected AbstractPortalResponse(String todo, String result) {
        this.todo = todo;
        this.result = result;
    }

    public boolean wasSuccessful() {
        return "ok".equals(result);
    }
}
