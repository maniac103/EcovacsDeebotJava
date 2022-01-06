package dev.pott.sucks.api.commands;

import org.w3c.dom.Node;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import dev.pott.sucks.api.dto.response.portal.AbstractPortalIotCommandResponse;
import dev.pott.sucks.api.dto.response.portal.PortalIotCommandJsonResponse;
import dev.pott.sucks.api.dto.response.portal.PortalIotCommandXmlResponse;

public class GetBatteryInfoCommand extends IotDeviceCommand<Integer> {
    public GetBatteryInfoCommand() {
        super("GetBatteryInfo", "getBattery");
    }

    @Override
    public Integer convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        if (response instanceof PortalIotCommandJsonResponse) {
            JsonResponse resp = ((PortalIotCommandJsonResponse) response).getResponsePayloadAs(gson,
                    JsonResponse.class);
            return resp.value;
        } else {
            String payload = ((PortalIotCommandXmlResponse) response).getResponsePayloadXml();
            Node batteryAttr = getFirstXPathMatch(payload, "//battery/@power");
            return Integer.valueOf(batteryAttr.getNodeValue());
        }
    }

    private static class JsonResponse {
        @SerializedName("value")
        public int value;
        @SerializedName("isLow")
        public int isLow;
    }
}
