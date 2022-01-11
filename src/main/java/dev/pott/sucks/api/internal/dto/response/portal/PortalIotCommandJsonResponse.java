package dev.pott.sucks.api.internal.dto.response.portal;

import org.eclipse.jdt.annotation.Nullable;

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

    public <T> T getResponsePayloadAs(Gson gson, Class<T> clazz) {
        JsonElement payloadRaw = getResponsePayload(gson);
        @Nullable
        T payload = gson.fromJson(payloadRaw, clazz);
        if (payload == null) {
            throw new NullPointerException();
        }
        return payload;
    }

    public JsonElement getResponsePayload(Gson gson) {
        @Nullable
        JsonResponsePayloadWrapper wrapper = gson.fromJson(response, JsonResponsePayloadWrapper.class);
        if (wrapper == null) {
            throw new NullPointerException();
        }
        return wrapper.body.payload;
    }

    public static class JsonPayloadHeader {
        @SerializedName("pri")
        public int pri;
        @SerializedName("ts")
        public long timestamp;
        @SerializedName("tzm")
        public int tzm;
        @SerializedName("fwVer")
        public String firmwareVersion;
        @SerializedName("hwVer")
        public String hardwareVersion;
    }

    public static class JsonResponsePayloadWrapper {
        @SerializedName("header")
        public JsonPayloadHeader header;
        @SerializedName("body")
        public JsonResponsePayloadBody body;
    }

    public static class JsonResponsePayloadBody {
        @SerializedName("code")
        public int code;
        @SerializedName("msg")
        public String message;
        @SerializedName("data")
        public JsonElement payload;
    }
}
