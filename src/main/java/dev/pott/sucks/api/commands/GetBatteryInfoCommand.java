package dev.pott.sucks.api.commands;

import org.w3c.dom.Node;

import com.google.gson.Gson;

import dev.pott.sucks.api.internal.dto.response.deviceapi.BatteryReport;
import dev.pott.sucks.api.internal.dto.response.portal.AbstractPortalIotCommandResponse;
import dev.pott.sucks.api.internal.dto.response.portal.PortalIotCommandJsonResponse;
import dev.pott.sucks.api.internal.dto.response.portal.PortalIotCommandXmlResponse;

public class GetBatteryInfoCommand extends IotDeviceCommand<Integer> {
    public GetBatteryInfoCommand() {
        super("GetBatteryInfo", "getBattery");
    }

    @Override
    public Integer convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        if (response instanceof PortalIotCommandJsonResponse) {
            BatteryReport resp = ((PortalIotCommandJsonResponse) response).getResponsePayloadAs(gson,
                    BatteryReport.class);
            return resp.percent;
        } else {
            String payload = ((PortalIotCommandXmlResponse) response).getResponsePayloadXml();
            Node batteryAttr = getFirstXPathMatch(payload, "//battery/@power");
            return Integer.valueOf(batteryAttr.getNodeValue());
        }
    }
}
