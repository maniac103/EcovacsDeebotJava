package dev.pott.sucks.api.dto.request.commands;

import com.google.gson.Gson;

import org.w3c.dom.Node;

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
        super("GetLifeSpan");
        this.type = type;
    }

    @Override
    public String getPayloadXml() {
        return "<ctl td=\"GetLifeSpan\" type=\"" + type.value + "\" />";
    }

    @Override
    public Integer convertResponse(String responsePayload, Gson gson) throws Exception {
        Node valueAttr = getFirstXPathMatch(responsePayload, "//ctl/@val");
        return Integer.valueOf(valueAttr.getNodeValue());
    }
}
