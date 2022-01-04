package dev.pott.sucks.api.dto.response.portal;

import java.util.Set;

import com.google.gson.annotations.SerializedName;

public class Device {
    @SerializedName("did")
    private final String did;

    @SerializedName("name")
    private final String name;

    @SerializedName("class")
    private final String deviceClass;

    @SerializedName("resource")
    private final String resource;

    @SerializedName("nick")
    private final String nick;

    @SerializedName("company")
    private final String company;

    @SerializedName("bindTs")
    private final long bindTs;

    @SerializedName("service")
    private final Service service;

    public Device(String did, String name, String deviceClass, String resource, String nick, String company,
            long bindTs, Service service) {
        this.did = did;
        this.name = name;
        this.deviceClass = deviceClass;
        this.resource = resource;
        this.nick = nick;
        this.company = company;
        this.bindTs = bindTs;
        this.service = service;
    }

    public String getDid() {
        return did;
    }

    public String getName() {
        return name;
    }

    public String getDeviceClass() {
        return deviceClass;
    }

    public String getResource() {
        return resource;
    }

    public String getNick() {
        return nick;
    }

    public String getCompany() {
        return company;
    }

    public long getBindTs() {
        return bindTs;
    }

    public Service getService() {
        return service;
    }

    public boolean usesMqtt() {
        return "eco-ng".equals(company);
    }

    public boolean usesJsonApi() {
        return DEVICE_CLASSES_USING_JSON_API.contains(deviceClass);
    }

    private static Set<String> DEVICE_CLASSES_USING_JSON_API = Set.of("yna5xi" /* Deebot 950 */,
            "h18jkh" /* Deebot T8 */, "fqxoiu" /* Deebot T8+ */, "ipzjy0" /* Deebot U2 */
    );
}
