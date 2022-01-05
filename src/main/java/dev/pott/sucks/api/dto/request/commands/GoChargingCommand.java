package dev.pott.sucks.api.dto.request.commands;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import dev.pott.sucks.api.dto.response.portal.AbstractPortalIotCommandResponse;

public class GoChargingCommand extends IotDeviceCommand<Void> {
    public GoChargingCommand() {
        super("Charge", "charge");
    }

    @Override
    public String getPayload(Gson gson, boolean asXml) {
        if (asXml) {
            return "<ctl td=\"Charge\"><charge type=\"go\"/></ctl>";
        }
        return getPayload(gson, asXml);
    }

    @Override
    protected Object getPayloadJsonArgs() {
        Map<String, String> args = new HashMap<>();
        args.put("act", "go");
        return args;
    }

    @Override
    public Void convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        return null;
    }
}
