package dev.pott.sucks.api.internal.dto.response.main;

import com.google.gson.annotations.SerializedName;

public class AuthCode {

    @SerializedName("ecovacsUid")
    private final String ecovacsUid;

    @SerializedName("authCode")
    private final String authCode;

    public AuthCode(String ecovacsUid, String authCode) {
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
