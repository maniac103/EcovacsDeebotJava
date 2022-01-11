package dev.pott.sucks.api.commands;

import com.google.gson.Gson;

import dev.pott.sucks.api.internal.dto.response.deviceapi.CachedMapInfoReport;
import dev.pott.sucks.api.internal.dto.response.portal.AbstractPortalIotCommandResponse;
import dev.pott.sucks.api.internal.dto.response.portal.PortalIotCommandJsonResponse;
import dev.pott.sucks.api.internal.dto.response.portal.PortalIotCommandXmlResponse;

public class GetActiveMapIdCommand extends IotDeviceCommand<String> {
    public GetActiveMapIdCommand() {
        super("GetMapM", "getCachedMapInfo");
    }

    @Override
    public String convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        if (response instanceof PortalIotCommandJsonResponse) {
            CachedMapInfoReport resp = ((PortalIotCommandJsonResponse) response).getResponsePayloadAs(gson,
                    CachedMapInfoReport.class);
            return resp.mapInfos.stream().filter(i -> i.used != 0).map(i -> i.mapId).findFirst().orElse("");
        } else {
            String payload = ((PortalIotCommandXmlResponse) response).getResponsePayloadXml();
            return getFirstXPathMatch(payload, "//@i").getNodeValue();
        }
    }
}
