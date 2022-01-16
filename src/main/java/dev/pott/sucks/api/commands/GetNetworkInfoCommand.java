package dev.pott.sucks.api.commands;

import org.w3c.dom.Node;

import com.google.gson.Gson;

import dev.pott.sucks.api.internal.dto.response.deviceapi.NetworkInfoReport;
import dev.pott.sucks.api.internal.dto.response.portal.AbstractPortalIotCommandResponse;
import dev.pott.sucks.api.internal.dto.response.portal.PortalIotCommandJsonResponse;
import dev.pott.sucks.api.internal.dto.response.portal.PortalIotCommandXmlResponse;
import dev.pott.sucks.cleaner.NetworkInfo;

public class GetNetworkInfoCommand extends IotDeviceCommand<NetworkInfo> {
    public GetNetworkInfoCommand() {
        super("GetNetInfo", "getNetInfo");
    }

    @Override
    public NetworkInfo convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        if (response instanceof PortalIotCommandJsonResponse) {
            NetworkInfoReport resp = ((PortalIotCommandJsonResponse) response).getResponsePayloadAs(gson,
                    NetworkInfoReport.class);
            return new NetworkInfo(resp.ip, resp.mac, resp.ssid, Integer.valueOf(resp.rssi));
        } else {
            String payload = ((PortalIotCommandXmlResponse) response).getResponsePayloadXml();
            Node ipAttr = getFirstXPathMatch(payload, "//@wi"); // TODO: verify this
            Node ssidAttr = getFirstXPathMatch(payload, "//@s");
            return new NetworkInfo(ipAttr.getNodeValue(), "", ssidAttr.getNodeValue(), 0);
        }
    }
}
