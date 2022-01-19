package dev.pott.sucks.api.commands;

import org.w3c.dom.Node;

import com.google.gson.Gson;

import dev.pott.sucks.api.internal.dto.response.deviceapi.SpeedReport;
import dev.pott.sucks.api.internal.dto.response.portal.AbstractPortalIotCommandResponse;
import dev.pott.sucks.api.internal.dto.response.portal.PortalIotCommandJsonResponse;
import dev.pott.sucks.api.internal.dto.response.portal.PortalIotCommandXmlResponse;
import dev.pott.sucks.cleaner.SuctionPower;

public class GetSuctionPowerCommand extends IotDeviceCommand<SuctionPower> {
    public GetSuctionPowerCommand() {
        super("GetCleanSpeed", "getSpeed");
    }

    @Override
    public SuctionPower convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        if (response instanceof PortalIotCommandJsonResponse) {
            SpeedReport resp = ((PortalIotCommandJsonResponse) response).getResponsePayloadAs(gson, SpeedReport.class);
            return SuctionPower.fromJsonValue(resp.speedLevel);
        } else {
            String payload = ((PortalIotCommandXmlResponse) response).getResponsePayloadXml();
            Node speedAttr = getFirstXPathMatch(payload, "//@speed"); // TODO: verify this
            return gson.fromJson(speedAttr.getNodeValue(), SuctionPower.class);
        }
    }
}
