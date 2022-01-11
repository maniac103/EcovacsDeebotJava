package dev.pott.sucks.api.commands;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import dev.pott.sucks.api.internal.dto.response.portal.AbstractPortalIotCommandResponse;
import dev.pott.sucks.api.internal.dto.response.portal.PortalIotCommandJsonResponse;
import dev.pott.sucks.api.internal.dto.response.portal.PortalIotCommandXmlResponse;

public class GetWaterSystemPresentCommand extends IotDeviceCommand<Boolean> {
    public GetWaterSystemPresentCommand() {
        super("GetWaterBoxInfo", "getWaterInfo");
    }

    public Boolean convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        if (response instanceof PortalIotCommandJsonResponse) {
            JsonResponse resp = ((PortalIotCommandJsonResponse) response).getResponsePayloadAs(gson,
                    JsonResponse.class);
            return resp.present != 0;
        } else {
            String payload = ((PortalIotCommandXmlResponse) response).getResponsePayloadXml();
            return Integer.valueOf(getFirstXPathMatch(payload, "//@on").getNodeValue()) != 0;
        }
    }

    private static class JsonResponse {
        @SerializedName("enabled")
        public int present;

        @SerializedName("amount")
        public int amount;
    }
}
