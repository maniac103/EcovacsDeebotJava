package dev.pott.sucks.api;

import dev.pott.sucks.api.dto.request.commands.IotDeviceCommand;

public interface EcovacsDevice {
    public String getId();
    public String getSerialNumber();
    public String getModelName();
    public String getFirmwareVersion();

    public <T> T sendCommand(IotDeviceCommand<T> command) throws EcovacsApiException;
}
