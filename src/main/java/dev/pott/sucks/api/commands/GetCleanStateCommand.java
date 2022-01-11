package dev.pott.sucks.api.commands;

import com.google.gson.Gson;

import dev.pott.sucks.api.internal.dto.response.deviceapi.CleanReport;
import dev.pott.sucks.api.internal.dto.response.portal.AbstractPortalIotCommandResponse;
import dev.pott.sucks.api.internal.dto.response.portal.PortalIotCommandJsonResponse;
import dev.pott.sucks.api.internal.dto.response.portal.PortalIotCommandXmlResponse;
import dev.pott.sucks.cleaner.CleanMode;

public class GetCleanStateCommand extends IotDeviceCommand<CleanMode> {
    public GetCleanStateCommand() {
        super("GetCleanState", "getCleanInfo");
    }

    @Override
    public CleanMode convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        if (response instanceof PortalIotCommandJsonResponse) {
            CleanReport resp = ((PortalIotCommandJsonResponse) response).getResponsePayloadAs(gson,
                    CleanReport.class);
            return resp.determineCleanMode(gson);
        } else {
            String payload = ((PortalIotCommandXmlResponse) response).getResponsePayloadXml();
            String mode = getFirstXPathMatch(payload, "//clean/@type").getNodeValue();
            return gson.fromJson(mode, CleanMode.class);
        }
    }
}
