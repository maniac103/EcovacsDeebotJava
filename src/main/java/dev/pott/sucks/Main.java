package dev.pott.sucks;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import com.google.gson.GsonBuilder;

import dev.pott.sucks.api.EcovacsApi;
import dev.pott.sucks.api.EcovacsApiConfiguration;
import dev.pott.sucks.api.EcovacsApiException;
import dev.pott.sucks.api.EcovacsDevice;
import dev.pott.sucks.cleaner.CleanMode;
import dev.pott.sucks.cleaner.SuctionPower;
import dev.pott.sucks.util.MD5Util;

public class Main {

    public static void main(String[] args) {
        SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();
        sslContextFactory.setTrustAll(true);
        HttpClient httpClient = new HttpClient(sslContextFactory);
        httpClient.setConnectTimeout(60);
        try {
            httpClient.start();
        } catch (Exception e) {
            return;
        }

        EcovacsApi api = new EcovacsApi(httpClient, new GsonBuilder().create(), new EcovacsApiConfiguration(
                MD5Util.getMD5Hash(String.valueOf(System.currentTimeMillis())), "user", "password", "EU", "DE", "EN"));
        EcovacsDevice.StateChangeListener listener = new EcovacsDevice.StateChangeListener() {
            @Override
            public void onBatteryLevelChanged(EcovacsDevice device, int newLevelPercent) {
                System.out.println(device.getSerialNumber() + ": Battery changed to " + newLevelPercent + "%");
            }

            @Override
            public void onChargingStateChanged(EcovacsDevice device, boolean charging) {
                System.out.println(
                        device.getSerialNumber() + ": Battery " + (charging ? "now" : "no longer") + " charging");
            }

            @Override
            public void onCleaningModeChanged(EcovacsDevice device, CleanMode newMode) {
                System.out.println(device.getSerialNumber() + ": Mode changed to " + newMode);
            }

            @Override
            public void onCleaningPowerChanged(EcovacsDevice device, SuctionPower newPower) {
                System.out.println(device.getSerialNumber() + ": Power changed to " + newPower);
            }

            @Override
            public void onCleaningStatsChanged(EcovacsDevice device, int cleanedArea, int cleaningTimeSeconds) {
                System.out.println(
                        device.getSerialNumber() + ": Cleaned " + cleanedArea + " m² in " + cleaningTimeSeconds + "s");
            }

            @Override
            public void onDeviceConnectionFailed(EcovacsDevice device, Throwable error) {
                System.out.println(device.getSerialNumber() + ": Connection failed");
                error.printStackTrace();
                System.exit(1);
            }
        };
        try {
            api.loginAndGetAccessToken();
            for (EcovacsDevice device : api.getDevices()) {
                System.out.println("Device " + device.getSerialNumber() + " is a " + device.getModelName() + ", FW "
                        + device.getFirmwareVersion());
                device.connect(listener);
            }
        } catch (EcovacsApiException e) {
            System.out.println("API failure:");
            e.printStackTrace();
        }
    }
}
