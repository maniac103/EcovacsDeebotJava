package dev.pott.sucks.api.dto.request.commands;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import dev.pott.sucks.api.dto.response.portal.AbstractPortalIotCommandResponse;

public class StopCommand extends IotDeviceCommand<Void> {
    public StopCommand() {
        super("stop", "clean");
    }

    @Override
    public String getPayload(Gson gson, boolean asXml) {
        if (asXml) {
            return "<ctl td=\"Clean\"><clean type=\"stop\" speed=\"standard\"/></ctl>";
        }
        return getPayload(gson, asXml);
    }

    @Override
    protected Map<String, String> getPayloadJsonArgs() {
        Map<String, String> args = new HashMap<>();
        args.put("act", "stop");
        return args;
    }

    @Override
    public Void convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        return null;
    }
}
