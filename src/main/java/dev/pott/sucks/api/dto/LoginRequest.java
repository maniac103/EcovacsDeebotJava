package dev.pott.sucks.api.dto;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("account")
    private final String username;
    @SerializedName("password")
    private final String password_hash;
    @SerializedName("requestId")
    private final String requestId;

    public LoginRequest(String username, String password_hash, String requestId){
        this.username = username;
        this.password_hash = password_hash;
        this.requestId = requestId;
    }
}
