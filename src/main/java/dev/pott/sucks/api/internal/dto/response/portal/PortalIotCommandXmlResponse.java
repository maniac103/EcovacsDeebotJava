package dev.pott.sucks.api.internal.dto.response.portal;

import com.google.gson.annotations.SerializedName;

public class PortalIotCommandXmlResponse extends AbstractPortalIotCommandResponse {
    @SerializedName("resp")
    private final String responseXml;

    public PortalIotCommandXmlResponse(String id, String result, String responseXml, String failureMessage) {
        super(id, result, failureMessage);
        this.responseXml = responseXml;
    }

    public String getResponsePayloadXml() {
        return responseXml;
    }
}
