package dev.pott.sucks.api.dto.response.main;

import com.google.gson.annotations.SerializedName;

public class ResponseWrapper<T> {
    @SerializedName("code")
    private final String code;

    @SerializedName("time")
    private final String time;

    @SerializedName("msg")
    private final String message;

    @SerializedName("data")
    private final T data;

    @SerializedName("success")
    private final boolean success;

    public ResponseWrapper(String code, String time, String message, T data, boolean success) {
        this.code = code;
        this.time = time;
        this.message = message;
        this.data = data;
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public String getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }
}
