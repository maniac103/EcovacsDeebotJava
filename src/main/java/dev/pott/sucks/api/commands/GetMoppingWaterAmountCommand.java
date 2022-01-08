package dev.pott.sucks.api.commands;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import dev.pott.sucks.api.dto.response.portal.AbstractPortalIotCommandResponse;
import dev.pott.sucks.api.dto.response.portal.PortalIotCommandJsonResponse;
import dev.pott.sucks.api.dto.response.portal.PortalIotCommandXmlResponse;
import dev.pott.sucks.cleaner.MoppingWaterAmount;

public class GetMoppingWaterAmountCommand extends IotDeviceCommand<MoppingWaterAmount> {
    public GetMoppingWaterAmountCommand() {
        super("GetWaterLevel", "getWaterInfo");
    }

    public MoppingWaterAmount convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        if (response instanceof PortalIotCommandJsonResponse) {
            JsonResponse resp = ((PortalIotCommandJsonResponse) response).getResponsePayloadAs(gson,
                    JsonResponse.class);
            return MoppingWaterAmount.fromApiValue(resp.amount);
        } else {
            String payload = ((PortalIotCommandXmlResponse) response).getResponsePayloadXml();
            return MoppingWaterAmount.fromApiValue(Integer.valueOf(getFirstXPathMatch(payload, "//@v").getNodeValue()));
        }
    }

    private static class JsonResponse {
        @SerializedName("enabled")
        public int present;

        @SerializedName("amount")
        public int amount;
    }
}
