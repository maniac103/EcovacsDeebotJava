package dev.pott.sucks.api.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;

import dev.pott.sucks.api.internal.dto.response.deviceapi.MapSetReport;
import dev.pott.sucks.api.internal.dto.response.portal.AbstractPortalIotCommandResponse;
import dev.pott.sucks.api.internal.dto.response.portal.PortalIotCommandJsonResponse;
import dev.pott.sucks.api.internal.dto.response.portal.PortalIotCommandXmlResponse;

public class GetMapSpotAreasWithMapIdCommand extends IotDeviceCommand<List<String>> {
    private final String mapId;

    public GetMapSpotAreasWithMapIdCommand(String mapId) {
        super("GetMapSet", "getMapSet");
        if (mapId == null) {
            throw new IllegalArgumentException("Map ID needs to be non-null");
        }
        this.mapId = mapId;
    }

    @Override
    protected void applyXmlPayload(Document doc, Element ctl) {
        ctl.setAttribute("tp", "sa");
    }

    @Override
    protected Object getJsonPayloadArgs() {
        Map<String, String> args = new HashMap<>();
        args.put("mid", mapId);
        args.put("type", "ar");
        return args;
    }

    @Override
    public List<String> convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        if (response instanceof PortalIotCommandJsonResponse) {
            MapSetReport resp = ((PortalIotCommandJsonResponse) response).getResponsePayloadAs(gson,
                    MapSetReport.class);
            return resp.subsets.stream().map(i -> i.id).collect(Collectors.toList());
        } else {
            String payload = ((PortalIotCommandXmlResponse) response).getResponsePayloadXml();
            NodeList mapIds = getXPathMatches(payload, "//m/@mid");
            List<String> result = new ArrayList<>();
            for (int i = 0; i < mapIds.getLength(); i++) {
                result.add(mapIds.item(i).getNodeValue());
            }
            return result;
        }
    }
}
