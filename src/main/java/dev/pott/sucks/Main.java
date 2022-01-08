package dev.pott.sucks;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.GsonBuilder;

import dev.pott.sucks.api.EcovacsApi;
import dev.pott.sucks.api.EcovacsApiConfiguration;
import dev.pott.sucks.api.EcovacsApiException;
import dev.pott.sucks.api.EcovacsDevice;
import dev.pott.sucks.cleaner.CleanMode;
import dev.pott.sucks.cleaner.MoppingWaterAmount;
import dev.pott.sucks.cleaner.SuctionPower;
import dev.pott.sucks.util.MD5Util;

public class Main {

    public static void main(String[] args) {
        final Logger logger = LoggerFactory.getLogger(Main.class);
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
                logger.info("{}: Battery changed to {} %", device.getSerialNumber(), newLevelPercent);
            }

            @Override
            public void onChargingStateChanged(EcovacsDevice device, boolean charging) {
                logger.info("{}: Battery {} charging", device.getSerialNumber(), charging ? "now" : "no longer");
            }

            @Override
            public void onCleaningModeChanged(EcovacsDevice device, CleanMode newMode) {
                logger.info("{}: Mode changed to {}", device.getSerialNumber(), newMode);
            }

            @Override
            public void onCleaningPowerChanged(EcovacsDevice device, SuctionPower newPower) {
                logger.info("{}: Power changed to {}", device.getSerialNumber(), newPower);
            }

            @Override
            public void onCleaningStatsChanged(EcovacsDevice device, int cleanedArea, int cleaningTimeSeconds) {
                logger.info("{}: Cleaned {} m² in {} s", device.getSerialNumber(), cleanedArea, cleaningTimeSeconds);
            }

            @Override
            public void onWaterSystemChanged(EcovacsDevice device, boolean present, MoppingWaterAmount amount) {
                logger.info("{}: Water system now {}, amount {}", device.getSerialNumber(),
                        present ? "present" : "missing", amount);
            }

            @Override
            public void onDeviceConnectionFailed(EcovacsDevice device, Throwable error) {
                logger.warn(device.getSerialNumber() + ": Connection failed", error);
                System.exit(1);
            }
        };
        try {
            api.loginAndGetAccessToken();
            for (EcovacsDevice device : api.getDevices()) {
                logger.info("Device {} is a {}, FW version {}", device.getSerialNumber(), device.getModelName(),
                        device.getFirmwareVersion());
                device.connect(listener);
            }
        } catch (EcovacsApiException e) {
            logger.warn("API failure", e);
            e.printStackTrace();
        }
    }
}
