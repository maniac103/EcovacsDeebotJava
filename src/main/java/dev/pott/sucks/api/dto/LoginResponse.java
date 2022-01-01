package dev.pott.sucks.api.dto;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("code")
    private final String code;

    @SerializedName("time")
    private final String time;

    public LoginResponse(String code, String time) {
        this.code = code;
        this.time = time;
    }

    public String getCode() {
        return code;
    }

    public String getTime() {
        return time;
    }
}
