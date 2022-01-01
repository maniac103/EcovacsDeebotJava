package dev.pott.sucks.api.dto;

import com.google.gson.annotations.SerializedName;

public class AuthCodeResponse {

    @SerializedName("ecovacsUid")
    private final String ecovacsUid;

    @SerializedName("authCode")
    private final String authCode;

    public AuthCodeResponse(String ecovacsUid, String authCode) {
        this.ecovacsUid = ecovacsUid;
        this.authCode = authCode;
    }

    public String getEcovacsUid() {
        return ecovacsUid;
    }

    public String getAuthCode() {
        return authCode;
    }
}
