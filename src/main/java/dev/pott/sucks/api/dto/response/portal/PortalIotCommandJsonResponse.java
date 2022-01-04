package dev.pott.sucks.api.dto.response.portal;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

public class PortalIotCommandJsonResponse extends AbstractPortalIotCommandResponse {
    @SerializedName("resp")
    private final JsonElement response;

    public PortalIotCommandJsonResponse(String id, String result, JsonElement response, String failureMessage) {
        super(id, result, failureMessage);
        this.response = response;
    }

    public <T> T getResponsePayloadAs(Gson gson, Type type) {
        return gson.fromJson(response, type);
    }
}
