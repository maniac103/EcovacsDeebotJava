package dev.pott.sucks.api.dto.response.portal;

import java.util.List;

import com.google.gson.annotations.SerializedName;

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
