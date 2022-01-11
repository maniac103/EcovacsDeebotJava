package dev.pott.sucks.api.commands;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import dev.pott.sucks.api.dto.response.portal.AbstractPortalIotCommandResponse;
import dev.pott.sucks.api.dto.response.portal.PortalIotCommandJsonResponse;

public class GetVolumeCommand extends IotDeviceCommand<Integer> {
    public GetVolumeCommand() {
        super("", "getVolume");
    }

    protected void applyXmlPayload(Document doc, Element ctl) {
        throw new IllegalStateException("Command only supported for JSON API");
    }

    public Integer convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        if (response instanceof PortalIotCommandJsonResponse) {
            JsonResponse resp = ((PortalIotCommandJsonResponse) response).getResponsePayloadAs(gson,
                    JsonResponse.class);
            return resp.volume;
        } else {
            // unsupported in XML case?
            return null;
        }
    }

    private static class JsonResponse {
        @SerializedName("volume")
        public int volume;

        @SerializedName("total")
        public int maxVolume;
    }
}
