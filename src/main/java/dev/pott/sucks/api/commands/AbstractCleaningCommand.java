package dev.pott.sucks.api.commands;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import dev.pott.sucks.api.dto.response.portal.AbstractPortalIotCommandResponse;

public abstract class AbstractCleaningCommand extends IotDeviceCommand<Void> {
    protected AbstractCleaningCommand() {
        super("Clean", "clean");
    }

    @Override
    protected void applyXmlPayload(Document doc, Element ctl) {
        Element clean = doc.createElement("clean");
        clean.setAttribute("type", getAction());
        clean.setAttribute("speed", "standard");
        ctl.appendChild(clean);
    }

    @Override
    protected Object getJsonPayloadArgs() {
        Map<String, String> args = new HashMap<>();
        args.put("act", getAction());
        return args;
    }

    @Override
    public Void convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        return null;
    }

    protected abstract String getAction();
}
