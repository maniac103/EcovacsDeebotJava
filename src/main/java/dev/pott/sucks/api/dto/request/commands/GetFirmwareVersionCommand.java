package dev.pott.sucks.api.dto.request.commands;

import com.google.gson.Gson;

import dev.pott.sucks.api.dto.response.portal.AbstractPortalIotCommandResponse;
import dev.pott.sucks.api.dto.response.portal.PortalIotCommandXmlResponse;

public class GetFirmwareVersionCommand extends IotDeviceCommand<String> {
    public GetFirmwareVersionCommand() {
        super("GetVersion", "GetVersion");
    }

    @Override
    public boolean forceXmlFormat() {
        return true;
    }

    @Override
    public String getPayload(Gson gson, boolean asXml) {
        return "<ctl td=\"GetVersion\" name=\"FW\"/>";
    }

    @Override
    public String convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        String payload = ((PortalIotCommandXmlResponse) response).getResponsePayloadXml();
        return getFirstXPathMatch(payload, "//ver[@name='FW']").getTextContent();
    }
}
