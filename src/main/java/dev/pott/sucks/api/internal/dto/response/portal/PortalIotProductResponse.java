package dev.pott.sucks.api.internal.dto.response.portal;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class PortalIotProductResponse {

    @SerializedName("code")
    private final int code;

    @SerializedName("data")
    private final List<IotProduct> products;

    public PortalIotProductResponse(int code, List<IotProduct> products) {
        this.code = code;
        this.products = products;
    }

    public List<IotProduct> getProducts() {
        return products;
    }
}
