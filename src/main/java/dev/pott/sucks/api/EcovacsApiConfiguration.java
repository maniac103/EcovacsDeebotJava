package dev.pott.sucks.api;

public final class EcovacsApiConfiguration {
    private final String deviceId;
    private final String username;
    private final String passwordHash;
    private final String continent;
    private final String country;

    public EcovacsApiConfiguration(String deviceId, String username, String passwordHash, String continent, String country) {
        this.deviceId = deviceId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.continent = continent;
        this.country = country;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getContinent() {
        return continent;
    }

    public String getCountry() {
        return country;
    }
}
