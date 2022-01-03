package dev.pott.sucks.api.dto.response.portal;

import com.google.gson.annotations.SerializedName;

public class IotProduct {
    @SerializedName("classid")
    private final String classId;

    @SerializedName("product")
    private final ProductDefinition productDef;

    public IotProduct(String classId, ProductDefinition productDef) {
        this.classId = classId;
        this.productDef = productDef;
    }

    public String getClassId() {
        return classId;
    }

    public ProductDefinition getDefinition() {
        return productDef;
    }

    public static class ProductDefinition {
        @SerializedName("_id")
        public final String id;

        @SerializedName("materialNo")
        public final String materialNumber;

        @SerializedName("name")
        public final String name;

        @SerializedName("icon")
        public final String icon;

        @SerializedName("iconUrl")
        public final String iconUrl;

        @SerializedName("model")
        public final String model;

        @SerializedName("UILogicId")
        public final String uiLogicId;

        @SerializedName("ota")
        public final boolean otaCapable;

        @SerializedName("supportType")
        public final SupportFlags supportFlags;

        public ProductDefinition(String id, String materialNumber, String name, String icon, String iconUrl,
                String model, String uiLogicId, boolean otaCapable, SupportFlags supportFlags) {
            this.id = id;
            this.materialNumber = materialNumber;
            this.name = name;
            this.icon = icon;
            this.iconUrl = iconUrl;
            this.model = model;
            this.uiLogicId = uiLogicId;
            this.otaCapable = otaCapable;
            this.supportFlags = supportFlags;
        }
    }

    public static class SupportFlags {
        @SerializedName("share")
        public final boolean canShare;

        @SerializedName("tmjl")
        public final boolean tmjl; // ???

        @SerializedName("assistant")
        public final boolean canUseAssistant;

        @SerializedName("alexa")
        public final boolean canUseAlexa;

        public SupportFlags(boolean share, boolean tmjl, boolean assistant, boolean alexa) {
            this.canShare = share;
            this.tmjl = tmjl;
            this.canUseAssistant = assistant;
            this.canUseAlexa = alexa;
        }
    }
}
