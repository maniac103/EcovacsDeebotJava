package dev.pott.sucks.api.commands;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.gson.Gson;

import dev.pott.sucks.api.dto.response.portal.AbstractPortalIotCommandResponse;
import dev.pott.sucks.cleaner.MoppingWaterAmount;

public class SetMoppingWaterAmountCommand extends IotDeviceCommand<Void> {
    private final int level;

    public SetMoppingWaterAmountCommand(MoppingWaterAmount amount) {
        super("SetWaterPermeability", "setWaterInfo");
        this.level = amount.toApiValue();
    }

    protected void applyXmlPayload(Document doc, Element ctl) {
        ctl.setAttribute("v", String.valueOf(level));
    }

    protected Object getJsonPayloadArgs() {
        Map<String, Object> args = new HashMap<>();
        args.put("amount", level);
        return args;
    }

    public Void convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        return null;
    }
}
