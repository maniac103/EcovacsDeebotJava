package dev.pott.sucks.api.commands;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gson.Gson;

import dev.pott.sucks.api.internal.dto.response.portal.AbstractPortalIotCommandResponse;
import dev.pott.sucks.api.internal.dto.response.portal.PortalIotCommandXmlResponse;

public class GetFirmwareVersionCommand extends IotDeviceCommand<String> {
    public GetFirmwareVersionCommand() {
        super("GetVersion", "GetVersion");
    }

    @Override
    public boolean forceXmlFormat() {
        return true;
    }

    @Override
    protected void applyXmlPayload(Document doc, Element ctl) {
        ctl.setAttribute("name", "FW");
    }

    @Override
    public String convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        String payload = ((PortalIotCommandXmlResponse) response).getResponsePayloadXml();
        return getFirstXPathMatch(payload, "//ver[@name='FW']").getTextContent();
    }
}
