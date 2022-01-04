package dev.pott.sucks;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import com.google.gson.GsonBuilder;

import dev.pott.sucks.api.EcovacsApi;
import dev.pott.sucks.api.EcovacsApiConfiguration;
import dev.pott.sucks.api.EcovacsApiException;
import dev.pott.sucks.api.EcovacsDevice;
import dev.pott.sucks.api.dto.request.commands.GetBatteryInfoCommand;
import dev.pott.sucks.api.dto.request.commands.GetChargeStateCommand;
import dev.pott.sucks.api.dto.request.commands.GetCleanStateCommand;
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
        try {
            api.loginAndGetAccessToken();
            for (EcovacsDevice device : api.getDevices()) {
                System.out.println("Device " + device.getSerialNumber() + " is a " + device.getModelName() + ", FW " + device.getFirmwareVersion());
                System.out.println(device.sendCommand(new GetChargeStateCommand()));
                System.out.println(device.sendCommand(new GetBatteryInfoCommand()));
                System.out.println(device.sendCommand(new GetCleanStateCommand()));
            }
        } catch (EcovacsApiException e) {
            System.out.println("API failure:");
            e.printStackTrace();
        }

        try {
            httpClient.stop();
        } catch (Exception e) {
        }
    }
}
