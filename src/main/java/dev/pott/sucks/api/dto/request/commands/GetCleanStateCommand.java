package dev.pott.sucks.api.dto.request.commands;

import com.google.gson.Gson;

import dev.pott.sucks.cleaner.CleanMode;
import dev.pott.sucks.cleaner.SuctionPower;

public class GetCleanStateCommand extends IotDeviceCommand<GetCleanStateCommand.CleaningState> {
    public GetCleanStateCommand() {
        super("GetCleanState");
    }

    @Override
    public CleaningState convertResponse(String responsePayload, Gson gson) throws Exception {
        String mode = getFirstXPathMatch(responsePayload, "//clean/@type").getNodeValue();
        String power = getFirstXPathMatch(responsePayload, "//clean/@speed").getNodeValue();
        return new CleaningState(gson.fromJson(mode, CleanMode.class), gson.fromJson(power, SuctionPower.class));
    }

    public static class CleaningState {
        public final CleanMode mode;
        public final SuctionPower power;

        CleaningState(CleanMode mode, SuctionPower power) {
            this.mode = mode;
            this.power = power;
        }
    }
}
