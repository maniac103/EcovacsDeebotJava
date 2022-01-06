package dev.pott.sucks.api.commands;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import dev.pott.sucks.api.dto.response.portal.AbstractPortalIotCommandResponse;
import dev.pott.sucks.api.dto.response.portal.PortalIotCommandJsonResponse;
import dev.pott.sucks.api.dto.response.portal.PortalIotCommandXmlResponse;

public class GetComponentLifeSpanCommand extends IotDeviceCommand<Integer> {
    public enum ComponentType {
        BRUSH("Brush", "brush"),
        SIDE_BRUSH("SideBrush", "sideBrush"),
        DUST_CASE_HEAP("DustCaseHeap", "heap");

        final String xmlValue;
        final String jsonValue;

        private ComponentType(String xmlValue, String jsonValue) {
            this.xmlValue = xmlValue;
            this.jsonValue = jsonValue;
        }
    }

    private final ComponentType type;

    public GetComponentLifeSpanCommand(ComponentType type) {
        super("GetLifeSpan", "getLifeSpan");
        this.type = type;
    }

    @Override
    protected void applyXmlPayload(Document doc, Element ctl) {
        ctl.setAttribute("type", type.xmlValue);
    }

    @Override
    protected Object getJsonPayloadArgs() {
        List<String> args = new ArrayList<>();
        args.add(type.jsonValue);
        return args;
    }

    @Override
    public Integer convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        if (response instanceof PortalIotCommandJsonResponse) {
            JsonElement respPayloadRaw = ((PortalIotCommandJsonResponse) response).getResponsePayload(gson);
            Type type = new TypeToken<List<JsonResponse>>() {
            }.getType();
            List<JsonResponse> resp = gson.fromJson(respPayloadRaw, type);
            if (resp == null || resp.isEmpty()) {
                throw new IllegalArgumentException("Invalid lifespan response " + respPayloadRaw);
            }
            return (int) Math.round(100.0 * resp.get(0).left / resp.get(0).total);
        } else {
            String payload = ((PortalIotCommandXmlResponse) response).getResponsePayloadXml();
            int value = nodeValueToInt(payload, "value");
            int total = nodeValueToInt(payload, "total");
            int left = nodeValueToInt(payload, "left");
            if (value >= 0 && total >= 0) {
                return (int) Math.round(100.0 * value / total);
            } else if (value >= 0) {
                return (int) Math.round(0.01 * value);
            } else if (left >= 0 && total >= 0) {
                return (int) Math.round(100.0 * left / total);
            } else if (left >= 0) {
                return (int) Math.round((double) left / 60.0);
            }
            return -1;
        }
    }

    private int nodeValueToInt(String payload, String attrName) throws Exception {
        Node attr = getFirstXPathMatch(payload, "//ctl/@" + attrName);
        return attr != null ? Integer.valueOf(attr.getNodeValue()) : -1;
    }

    private static class JsonResponse {
        @SerializedName("type")
        public String brush;

        @SerializedName("left")
        public int left;

        @SerializedName("total")
        public int total;
    }
}
