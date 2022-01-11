package dev.pott.sucks.api.commands;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gson.Gson;

import dev.pott.sucks.api.internal.dto.response.portal.AbstractPortalIotCommandResponse;

public class SpotAreaCleaningCommand extends IotDeviceCommand<Void> {
    private final String content;
    private final int cleanPasses;

    public SpotAreaCleaningCommand(String roomIds, int cleanPasses) {
        super("Clean", "clean");
        this.content = roomIds;
        this.cleanPasses = cleanPasses;
    }

    @Override
    protected void applyXmlPayload(Document doc, Element ctl) {
        Element clean = doc.createElement("clean");
        clean.setAttribute("act", "s");
        clean.setAttribute("type", "SpotArea");
        clean.setAttribute("speed", "standard");
        clean.setAttribute("mid", content);
        clean.setAttribute("deep", String.valueOf(cleanPasses));
        ctl.appendChild(clean);
    }

    @Override
    protected Object getJsonPayloadArgs() {
        Map<String, Object> args = new HashMap<>();
        args.put("act", "start");
        args.put("content", content);
        args.put("count", cleanPasses);
        args.put("type", "spotArea");
        return args;
    }

    @Override
    public Void convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        return null;
    }
}
