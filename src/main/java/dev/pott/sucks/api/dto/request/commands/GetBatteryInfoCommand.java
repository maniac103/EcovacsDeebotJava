package dev.pott.sucks.api.dto.request.commands;

import com.google.gson.Gson;

import org.w3c.dom.Node;

public class GetBatteryInfoCommand extends IotDeviceCommand<Integer> {
    public GetBatteryInfoCommand() {
        super("GetBatteryInfo");
    }

    @Override
    public Integer convertResponse(String responsePayload, Gson gson) throws Exception {
        Node batteryAttr = getFirstXPathMatch(responsePayload, "//battery/@power");
        return Integer.valueOf(batteryAttr.getNodeValue());
    }
}
