package dev.pott.sucks.api.dto.request.commands;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import dev.pott.sucks.api.dto.response.portal.AbstractPortalIotCommandResponse;

public class StartCleaningCommand extends IotDeviceCommand<Void> {
    public StartCleaningCommand() {
        super("Clean", "clean");
    }

    @Override
    public String getPayload(Gson gson, boolean asXml) {
        if (asXml) {
            return "<ctl td=\"Clean\"><clean type=\"auto\" speed=\"standard\"/></ctl>";
        }
        return super.getPayload(gson, asXml);
    }

    @Override
    protected Object getPayloadJsonArgs() {
        Map<String, String> args = new HashMap<>();
        args.put("type", "auto");
        args.put("act", "start");
        return args;
    }

    @Override
    public Void convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        return null;
    }
}
