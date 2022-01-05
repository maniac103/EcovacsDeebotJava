package dev.pott.sucks.api.dto.request.commands;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import dev.pott.sucks.api.dto.response.portal.AbstractPortalIotCommandResponse;
import dev.pott.sucks.api.dto.response.portal.PortalIotCommandJsonResponse;
import dev.pott.sucks.api.dto.response.portal.PortalIotCommandXmlResponse;
import dev.pott.sucks.cleaner.CleanMode;

public class GetCleanStateCommand extends IotDeviceCommand<CleanMode> {
    public GetCleanStateCommand() {
        super("GetCleanState", "getCleanInfo");
    }

    @Override
    public CleanMode convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        if (response instanceof PortalIotCommandJsonResponse) {
            JsonResponse resp = ((PortalIotCommandJsonResponse) response).getResponsePayloadAs(gson,
                    JsonResponse.class);
            return resp.state;
        } else {
            String payload = ((PortalIotCommandXmlResponse) response).getResponsePayloadXml();
            String mode = getFirstXPathMatch(payload, "//clean/@type").getNodeValue();
            return gson.fromJson(mode, CleanMode.class);
        }
    }

    private static class JsonResponse {
        @SerializedName("trigger")
        public String trigger;
        @SerializedName("state")
        public CleanMode state;
    }
}
