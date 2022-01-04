package dev.pott.sucks.api.dto.request.commands;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Node;

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
            Type type = new TypeToken<JsonResponsePayloadWrapper<JsonResponse>>(){}.getType();
            JsonResponsePayloadWrapper<JsonResponse> wrapper = ((PortalIotCommandJsonResponse) response).getResponsePayloadAs(gson, type);
            return wrapper.body.payload.value;
        } else {
            String payload = ((PortalIotCommandXmlResponse) response).getResponsePayloadXml();
            Node batteryAttr = getFirstXPathMatch(payload, "//battery/@power");
            return Integer.valueOf(batteryAttr.getNodeValue());
        }
    }

    private static class JsonResponse {
        @SerializedName("value") public int value;
        @SerializedName("isLow") public int isLow;
    }
}
