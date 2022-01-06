package dev.pott.sucks.api.commands;

import com.google.gson.Gson;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
    protected void applyXmlPayload(Document doc, Element ctl) {
        ctl.setAttribute("name", "FW");
    }

    @Override
    public String convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        String payload = ((PortalIotCommandXmlResponse) response).getResponsePayloadXml();
        return getFirstXPathMatch(payload, "//ver[@name='FW']").getTextContent();
    }
}
