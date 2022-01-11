package dev.pott.sucks.api.commands;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gson.Gson;

import dev.pott.sucks.api.internal.dto.response.portal.AbstractPortalIotCommandResponse;

public class GoChargingCommand extends IotDeviceCommand<Void> {
    public GoChargingCommand() {
        super("Charge", "charge");
    }

    @Override
    protected void applyXmlPayload(Document doc, Element ctl) {
        Element charge = doc.createElement("charge");
        charge.setAttribute("type", "go");
        ctl.appendChild(charge);
    }

    @Override
    protected Object getJsonPayloadArgs() {
        Map<String, String> args = new HashMap<>();
        args.put("act", "go");
        return args;
    }

    @Override
    public Void convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        return null;
    }
}
