package dev.pott.sucks.api.commands;

import com.google.gson.Gson;

import dev.pott.sucks.api.internal.dto.response.deviceapi.WaterInfoReport;
import dev.pott.sucks.api.internal.dto.response.portal.AbstractPortalIotCommandResponse;
import dev.pott.sucks.api.internal.dto.response.portal.PortalIotCommandJsonResponse;
import dev.pott.sucks.api.internal.dto.response.portal.PortalIotCommandXmlResponse;
import dev.pott.sucks.cleaner.MoppingWaterAmount;

public class GetMoppingWaterAmountCommand extends IotDeviceCommand<MoppingWaterAmount> {
    public GetMoppingWaterAmountCommand() {
        super("GetWaterLevel", "getWaterInfo");
    }

    public MoppingWaterAmount convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        if (response instanceof PortalIotCommandJsonResponse) {
            WaterInfoReport resp = ((PortalIotCommandJsonResponse) response).getResponsePayloadAs(gson,
                    WaterInfoReport.class);
            return MoppingWaterAmount.fromApiValue(resp.waterAmount);
        } else {
            String payload = ((PortalIotCommandXmlResponse) response).getResponsePayloadXml();
            return MoppingWaterAmount.fromApiValue(Integer.valueOf(getFirstXPathMatch(payload, "//@v").getNodeValue()));
        }
    }
}
