package dev.pott.sucks.api;

public final class EcovacsApiConfiguration {
    private final String deviceId;
    private final String username;
    private final String password;
    private final String continent;
    private final String country;

    public EcovacsApiConfiguration(String deviceId,String username, String password, String continent, String country) {
        this.deviceId = deviceId;
        this.username = username;
        this.password = password;
        this.continent = continent;
        this.country = country;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getContinent() {
        return continent;
    }

    public String getCountry() {
        return country;
    }

    public String getResource() {
        return deviceId.substring(0, 8);
    }
}
