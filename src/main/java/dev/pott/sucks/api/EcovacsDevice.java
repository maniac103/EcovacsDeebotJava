package dev.pott.sucks.api;

import org.eclipse.jdt.annotation.NonNullByDefault;

import dev.pott.sucks.api.commands.IotDeviceCommand;
import dev.pott.sucks.api.commands.MultiCommand;
import dev.pott.sucks.cleaner.CleanMode;
import dev.pott.sucks.cleaner.DeviceCapability;
import dev.pott.sucks.cleaner.ErrorDescription;
import dev.pott.sucks.cleaner.MoppingWaterAmount;
import dev.pott.sucks.cleaner.SuctionPower;

@NonNullByDefault
public interface EcovacsDevice {
    public interface StateChangeListener {
        void onBatteryLevelChanged(EcovacsDevice device, int newLevelPercent);

        void onChargingStateChanged(EcovacsDevice device, boolean charging);

        void onCleaningModeChanged(EcovacsDevice device, CleanMode newMode);

        void onCleaningPowerChanged(EcovacsDevice device, SuctionPower newPower);

        void onCleaningStatsChanged(EcovacsDevice device, int cleanedArea, int cleaningTimeSeconds);

        void onWaterSystemChanged(EcovacsDevice device, boolean present, MoppingWaterAmount amount);

        void onErrorReported(EcovacsDevice device, ErrorDescription error);

        void onDeviceConnectionFailed(EcovacsDevice device, Throwable error);
    }

    public String getId();

    public String getSerialNumber();

    public String getModelName();

    public String getFirmwareVersion();

    public boolean hasCapability(DeviceCapability cap);

    public void connect(StateChangeListener listener) throws EcovacsApiException;

    public void disconnect();

    public <T> T sendCommand(IotDeviceCommand<T> command) throws EcovacsApiException;

    public <T> T sendCommand(MultiCommand<T> command) throws EcovacsApiException;
}
