package dev.pott.sucks.cleaner;

public class NetworkInfo {
    public final String ipAddress;
    public final String macAddress;
    public final String wifiSsid;
    public final int wifiRssi;

    public NetworkInfo(String ip, String mac, String ssid, int rssi) {
        this.ipAddress = ip;
        this.macAddress = mac;
        this.wifiSsid = ssid;
        this.wifiRssi = rssi;
    }
}
