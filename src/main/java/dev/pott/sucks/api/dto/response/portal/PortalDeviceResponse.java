package dev.pott.sucks.api.dto.response.portal;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PortalDeviceResponse extends AbstractPortalResponse {

    @SerializedName("devices")
    private final List<Device> devices;

    public PortalDeviceResponse(String todo, String result, List<Device> devices) {
        super(todo, result);
        this.devices = devices;
    }

    public List<Device> getDevices() {
        return devices;
    }
}
