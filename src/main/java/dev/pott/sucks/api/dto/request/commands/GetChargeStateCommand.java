package dev.pott.sucks.api.dto.request.commands;

import com.google.gson.Gson;

import org.w3c.dom.Node;

import dev.pott.sucks.cleaner.ChargeMode;

public class GetChargeStateCommand extends IotDeviceCommand<ChargeMode> {
    public GetChargeStateCommand() {
        super("GetChargeState");
    }

    @Override
    public String getPayloadXml() {
        return "<query xmlns=\"com:ctl\"><ctl td=\"GetChargeState\" />";
    }

    @Override
    public ChargeMode convertResponse(String responsePayload, Gson gson) throws Exception {
        Node typeAttr = getFirstXPathMatch(responsePayload, "//charge/@type");
        return gson.fromJson(typeAttr.getNodeValue(), ChargeMode.class);
    }
}
