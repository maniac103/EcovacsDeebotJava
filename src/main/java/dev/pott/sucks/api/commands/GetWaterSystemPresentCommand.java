package dev.pott.sucks.api.commands;

import com.google.gson.Gson;

import dev.pott.sucks.api.internal.dto.response.deviceapi.WaterInfoReport;
import dev.pott.sucks.api.internal.dto.response.portal.AbstractPortalIotCommandResponse;
import dev.pott.sucks.api.internal.dto.response.portal.PortalIotCommandJsonResponse;
import dev.pott.sucks.api.internal.dto.response.portal.PortalIotCommandXmlResponse;

public class GetWaterSystemPresentCommand extends IotDeviceCommand<Boolean> {
    public GetWaterSystemPresentCommand() {
        super("GetWaterBoxInfo", "getWaterInfo");
    }

    public Boolean convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        if (response instanceof PortalIotCommandJsonResponse) {
            WaterInfoReport resp = ((PortalIotCommandJsonResponse) response).getResponsePayloadAs(gson,
                    WaterInfoReport.class);
            return resp.waterPlatePresent != 0;
        } else {
            String payload = ((PortalIotCommandXmlResponse) response).getResponsePayloadXml();
            return Integer.valueOf(getFirstXPathMatch(payload, "//@on").getNodeValue()) != 0;
        }
    }
}
