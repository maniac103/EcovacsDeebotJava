package dev.pott.sucks.api.commands;

import org.w3c.dom.Node;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import dev.pott.sucks.api.dto.response.portal.AbstractPortalIotCommandResponse;
import dev.pott.sucks.api.dto.response.portal.PortalIotCommandJsonResponse;
import dev.pott.sucks.api.dto.response.portal.PortalIotCommandXmlResponse;
import dev.pott.sucks.cleaner.ChargeMode;

public class GetChargeStateCommand extends IotDeviceCommand<ChargeMode> {
    public GetChargeStateCommand() {
        super("GetChargeState", "getChargeState");
    }

    @Override
    public ChargeMode convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        if (response instanceof PortalIotCommandJsonResponse) {
            JsonResponse resp = ((PortalIotCommandJsonResponse) response).getResponsePayloadAs(gson,
                    JsonResponse.class);
            return resp.charging != 0 ? ChargeMode.CHARGING : ChargeMode.IDLE;
        } else {
            String payload = ((PortalIotCommandXmlResponse) response).getResponsePayloadXml();
            Node typeAttr = getFirstXPathMatch(payload, "//charge/@type");
            return gson.fromJson(typeAttr.getNodeValue(), ChargeMode.class);
        }
    }

    private static class JsonResponse {
        @SerializedName("isCharging")
        int charging;
        @SerializedName("mode")
        String mode;
    }
}
