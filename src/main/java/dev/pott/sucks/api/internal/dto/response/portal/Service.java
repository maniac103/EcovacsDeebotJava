package dev.pott.sucks.api.internal.dto.response.portal;

import com.google.gson.annotations.SerializedName;

public class Service {

    @SerializedName("jmq")
    private final String jmq;

    @SerializedName("mqs")
    private final String mqs;

    public Service(String jmq, String mqs) {
        this.jmq = jmq;
        this.mqs = mqs;
    }

    public String getJmq() {
        return jmq;
    }

    public String getMqs() {
        return mqs;
    }
}
