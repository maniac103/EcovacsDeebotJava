package dev.pott.sucks.api;

import com.google.gson.annotations.SerializedName;

public enum PortalTodo {
    @SerializedName("GetDeviceList") GET_DEVICE_LIST,
    @SerializedName("loginByItToken") LOGIN_BY_TOKEN;
}
