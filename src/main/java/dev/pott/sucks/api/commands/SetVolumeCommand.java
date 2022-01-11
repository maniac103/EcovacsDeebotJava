package dev.pott.sucks.api.commands;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import dev.pott.sucks.api.dto.response.portal.AbstractPortalIotCommandResponse;

public class SetVolumeCommand extends IotDeviceCommand<Void> {
    private final int volume;

    public SetVolumeCommand(int volume) {
        super("", "setVolume");
        if (volume < 0 || volume > 10) {
            throw new IllegalArgumentException("Volume must be between 0 and 10");
        }
        this.volume = volume;
    }

    protected Object getJsonPayloadArgs() {
        Map<String, Object> args = new HashMap<>();
        args.put("volume", volume);
        return args;
    }

    protected void applyXmlPayload(Document doc, Element ctl) {
        throw new IllegalStateException("Command only supported for JSON API");
    }

    public Void convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        return null;
    }
}
