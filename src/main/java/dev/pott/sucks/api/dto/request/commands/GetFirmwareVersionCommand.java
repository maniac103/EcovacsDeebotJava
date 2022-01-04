package dev.pott.sucks.api.dto.request.commands;

import com.google.gson.Gson;

public class GetFirmwareVersionCommand extends IotDeviceCommand<String> {
    public GetFirmwareVersionCommand() {
        super("GetVersion");
    }

    @Override
    public String getPayloadXml() {
        return "<ctl td=\"GetVersion\" name=\"FW\"/>";
    }

    @Override
    public String convertResponse(String responsePayload, Gson gson) throws Exception {
        return getFirstXPathMatch(responsePayload,  "//ver[@name='FW']").getTextContent();
    }
}
