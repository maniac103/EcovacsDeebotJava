package dev.pott.sucks.api.dto.request.commands;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Node;

import dev.pott.sucks.api.dto.response.portal.AbstractPortalIotCommandResponse;
import dev.pott.sucks.api.dto.response.portal.PortalIotCommandJsonResponse;
import dev.pott.sucks.api.dto.response.portal.PortalIotCommandXmlResponse;
import dev.pott.sucks.cleaner.ChargeMode;

public class GetChargeStateCommand extends IotDeviceCommand<ChargeMode> {
    public GetChargeStateCommand() {
        super("GetChargeState", "getChargeState");
    }

    @Override
    public String getPayload(Gson gson, boolean asXml) {
        if (asXml) {
            return "<query xmlns=\"com:ctl\"><ctl td=\"GetChargeState\" />";
        }
        return super.getPayload(gson, asXml);
    }

    @Override
    public ChargeMode convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        if (response instanceof PortalIotCommandJsonResponse) {
            Type type = new TypeToken<JsonResponsePayloadWrapper<JsonResponse>>(){}.getType();
            JsonResponsePayloadWrapper<JsonResponse> wrapper = ((PortalIotCommandJsonResponse) response).getResponsePayloadAs(gson, type);
            return wrapper.body.payload.charging != 0 ? ChargeMode.CHARGING : ChargeMode.IDLE;
        } else {
            String payload = ((PortalIotCommandXmlResponse) response).getResponsePayloadXml();
            Node typeAttr = getFirstXPathMatch(payload, "//charge/@type");
            return gson.fromJson(typeAttr.getNodeValue(), ChargeMode.class);
        }
    }

    private static class JsonResponse {
        @SerializedName("isCharging") int charging;
        @SerializedName("mode") String mode;
    }
}
