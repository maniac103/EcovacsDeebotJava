package dev.pott.sucks.api.commands;

import com.google.gson.Gson;

import dev.pott.sucks.api.internal.dto.response.deviceapi.ErrorReport;
import dev.pott.sucks.api.internal.dto.response.portal.AbstractPortalIotCommandResponse;
import dev.pott.sucks.api.internal.dto.response.portal.PortalIotCommandJsonResponse;
import dev.pott.sucks.api.internal.dto.response.portal.PortalIotCommandXmlResponse;
import dev.pott.sucks.cleaner.ErrorDescription;

public class GetErrorCommand extends IotDeviceCommand<ErrorDescription> {
    public GetErrorCommand() {
        super("GetError", "getError");
    }

    @Override
    public ErrorDescription convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        if (response instanceof PortalIotCommandJsonResponse) {
            ErrorReport resp = ((PortalIotCommandJsonResponse) response).getResponsePayloadAs(gson, ErrorReport.class);
            if (resp.errorCodes.isEmpty()) {
                return null;
            }
            return new ErrorDescription(resp.errorCodes.get(0));
        } else {
            String payload = ((PortalIotCommandXmlResponse) response).getResponsePayloadXml();
            int errorCode = Integer.valueOf(getFirstXPathMatch(payload, "//@errs").getNodeValue());
            return new ErrorDescription(errorCode);
        }
    }
}
