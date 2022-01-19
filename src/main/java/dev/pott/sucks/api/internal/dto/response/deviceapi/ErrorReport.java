package dev.pott.sucks.api.internal.dto.response.deviceapi;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ErrorReport {
    @SerializedName("code")
    public List<Integer> errorCodes;
}
