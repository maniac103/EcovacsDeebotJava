package dev.pott.sucks.api.commands;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import dev.pott.sucks.api.dto.response.portal.AbstractPortalIotCommandResponse;
import dev.pott.sucks.api.dto.response.portal.PortalIotCommandJsonResponse;
import dev.pott.sucks.api.dto.response.portal.PortalIotCommandXmlResponse;

public class GetTotalStatsCommand extends IotDeviceCommand<GetTotalStatsCommand.TotalStats> {
    public class TotalStats {
        @SerializedName("area")
        public final int totalArea;
        @SerializedName("time")
        public final int totalRuntime;
        @SerializedName("count")
        public final int cleanRuns;

        private TotalStats(int area, int runtime, int runs) {
            this.totalArea = area;
            this.totalRuntime = runtime;
            this.cleanRuns = runs;
        }
    }

    public GetTotalStatsCommand() {
        super("GetCleanSum", "getTotalStats");
    }

    public TotalStats convertResponse(AbstractPortalIotCommandResponse response, Gson gson) throws Exception {
        if (response instanceof PortalIotCommandJsonResponse) {
            return ((PortalIotCommandJsonResponse) response).getResponsePayloadAs(gson, TotalStats.class);
        } else {
            String payload = ((PortalIotCommandXmlResponse) response).getResponsePayloadXml();
            String area = getFirstXPathMatch(payload, "//@a").getNodeValue();
            String time = getFirstXPathMatch(payload, "//@l").getNodeValue();
            String count = getFirstXPathMatch(payload, "//@c").getNodeValue();
            return new TotalStats(Integer.valueOf(area), Integer.valueOf(time), Integer.valueOf(count));
        }
    }

}
