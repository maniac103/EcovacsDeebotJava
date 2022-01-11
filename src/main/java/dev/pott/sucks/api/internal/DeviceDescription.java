package dev.pott.sucks.api.internal;

import java.util.Set;

import dev.pott.sucks.cleaner.DeviceCapability;

public class DeviceDescription {
    public final String modelName;
    public final String deviceClass;
    public final String deviceClassLink;
    public final boolean usesJsonApi;
    public final boolean usesMqtt;
    public final Set<DeviceCapability> capabilities;

    public DeviceDescription(String modelName, String deviceClass, String deviceClassLink, boolean usesJsonApi,
            boolean usesMqtt, Set<DeviceCapability> capabilities) {
        this.modelName = modelName;
        this.capabilities = capabilities;
        this.deviceClass = deviceClass;
        this.deviceClassLink = deviceClassLink;
        this.usesJsonApi = usesJsonApi;
        this.usesMqtt = usesMqtt;
    }

    public DeviceDescription resolveLinkWith(DeviceDescription other) {
        return new DeviceDescription(modelName, deviceClass, null, other.usesJsonApi, other.usesMqtt,
                other.capabilities);
    }
}
