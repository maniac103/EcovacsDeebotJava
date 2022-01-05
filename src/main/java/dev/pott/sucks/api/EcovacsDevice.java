package dev.pott.sucks.api;

import org.eclipse.jdt.annotation.NonNullByDefault;

import dev.pott.sucks.api.dto.request.commands.IotDeviceCommand;
import dev.pott.sucks.cleaner.CleanMode;
import dev.pott.sucks.cleaner.SuctionPower;

@NonNullByDefault
public interface EcovacsDevice {
    public interface StateChangeListener {
        void onBatteryLevelChanged(EcovacsDevice device, int newLevelPercent);

        void onChargingStateChanged(EcovacsDevice device, boolean charging);

        void onCleaningModeChanged(EcovacsDevice device, CleanMode newMode);

        void onCleaningPowerChanged(EcovacsDevice device, SuctionPower newPower);
    }

    public String getId();

    public String getSerialNumber();

    public String getModelName();

    public String getFirmwareVersion();

    public void connect(StateChangeListener listener) throws EcovacsApiException;

    public void disconnect();

    public <T> T sendCommand(IotDeviceCommand<T> command) throws EcovacsApiException;
}
