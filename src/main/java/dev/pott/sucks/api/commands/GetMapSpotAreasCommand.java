package dev.pott.sucks.api.commands;

import java.util.List;

public class GetMapSpotAreasCommand implements MultiCommand<List<String>> {
    private boolean requestingMapInfo;
    private List<String> spotAreas;

    @Override
    public IotDeviceCommand<?> getFirstCommand(boolean useXml) {
        requestingMapInfo = true;
        return new GetActiveMapIdCommand();
    }

    @Override
    public IotDeviceCommand<?> processResultAndGetNextCommand(Object lastCommandResult) {
        if (requestingMapInfo) {
            requestingMapInfo = false;
            return new GetMapSpotAreasWithMapIdCommand((String) lastCommandResult);
        } else {
            spotAreas = (List<String>) lastCommandResult;
            return null;
        }
    }

    @Override
    public List<String> getResult() {
        return spotAreas;
    }
}
