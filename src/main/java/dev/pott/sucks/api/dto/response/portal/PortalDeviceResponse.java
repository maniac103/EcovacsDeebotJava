package dev.pott.sucks.api.dto.response.portal;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PortalDeviceResponse {

    @SerializedName("todo")
    private final String todo;

    @SerializedName("result")
    private final String result;

    @SerializedName("devices")
    private final List<Device> devices;

    public PortalDeviceResponse(String todo, String result, List<Device> devices) {
        this.todo = todo;
        this.result = result;
        this.devices = devices;
    }

    public String getTodo() {
        return todo;
    }

    public String getResult() {
        return result;
    }

    public List<Device> getDevices() {
        return devices;
    }
}
