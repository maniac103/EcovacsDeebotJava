package dev.pott.sucks.api.dto.request.commands;

import org.w3c.dom.Node;

import com.google.gson.Gson;

import dev.pott.sucks.api.dto.response.portal.AbstractPortalIotCommandResponse;
import dev.pott.sucks.api.dto.response.portal.PortalIotCommandJsonResponse;
import dev.pott.sucks.api.dto.response.portal.PortalIotCommandXmlResponse;

public class GetComponentLifeSpanCommand extends IotDeviceCommand<Integer> {
    public enum Type {
        BRUSH("Brush"),
        SIDE_BRUSH("SideBrush"),
        DUST_CASE_HEAP("DustCaseHeap");

        final String value;

        private Type(String value) {
            this.value = value;
        }
    }

    private final Type type;

    public GetComponentLifeSpanCommand(Type type) {
        super("GetLifeSpan", "getLifeSpan");
        this.type = type;
    }

    @Override
    public String getPayload(Gson gson, boolean asXml) {
        // FIXME
        return "<ctl td=\"GetLifeSpan\" type=\"" + type.value + "\" />";
    }

    @Override
    public Integer convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        if (response instanceof PortalIotCommandJsonResponse) {
            // FIXME
            return null;
        } else {
            String payload = ((PortalIotCommandXmlResponse) response).getResponsePayloadXml();
            Node valueAttr = getFirstXPathMatch(payload, "//ctl/@val");
            return Integer.valueOf(valueAttr.getNodeValue());
        }
    }
}
