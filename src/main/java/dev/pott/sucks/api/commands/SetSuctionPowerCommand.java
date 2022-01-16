package dev.pott.sucks.api.commands;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gson.Gson;

import dev.pott.sucks.api.internal.dto.response.portal.AbstractPortalIotCommandResponse;
import dev.pott.sucks.cleaner.SuctionPower;

public class SetSuctionPowerCommand extends IotDeviceCommand<Void> {
    private final SuctionPower power;

    public SetSuctionPowerCommand(SuctionPower power) {
        super("SetCleanSpeed", "setSpeed");
        this.power = power;
    }

    protected Object getJsonPayloadArgs() {
        Map<String, Object> args = new HashMap<>();
        args.put("speed", power.toJsonValue());
        return args;
    }

    protected void applyXmlPayload(Document doc, Element ctl) {
        ctl.setAttribute("speed", power.toXmlValue());
    }

    public Void convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        return null;
    }
}
