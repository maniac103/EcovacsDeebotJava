package dev.pott.sucks.api.dto.response.portal;

import com.google.gson.annotations.SerializedName;

public class PortalIotCommandResponse {
    @SerializedName("id")
    private final String id;

    @SerializedName("resp")
    private final String response;

    @SerializedName("ret")
    private final String result;

    @SerializedName("debug")
    private final String failureMessage;

    public PortalIotCommandResponse(String id, String result, String response, String failureMessage) {
        this.id = id;
        this.result = result;
        this.response = response;
        this.failureMessage = failureMessage;
    }

    public boolean wasSuccessful() {
        return "ok".equals(result);
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public String getResponsePayload() {
        return response;
    }
}
