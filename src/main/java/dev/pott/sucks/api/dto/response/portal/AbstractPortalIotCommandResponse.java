package dev.pott.sucks.api.dto.response.portal;

import com.google.gson.annotations.SerializedName;

public class AbstractPortalIotCommandResponse {
    @SerializedName("id")
    private final String id;

    @SerializedName("ret")
    private final String result;

    @SerializedName("debug")
    private final String failureMessage;

    public AbstractPortalIotCommandResponse(String id, String result, String failureMessage) {
        this.id = id;
        this.result = result;
        this.failureMessage = failureMessage;
    }

    public boolean wasSuccessful() {
        return "ok".equals(result);
    }

    public String getFailureMessage() {
        return failureMessage;
    }
}
