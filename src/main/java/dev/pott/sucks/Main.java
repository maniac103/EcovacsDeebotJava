package dev.pott.sucks;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import com.google.gson.GsonBuilder;

import dev.pott.sucks.api.EcovacsApi;
import dev.pott.sucks.api.EcovacsApiConfiguration;
import dev.pott.sucks.api.EcovacsApiException;
import dev.pott.sucks.api.dto.response.portal.Device;
import dev.pott.sucks.api.dto.response.portal.IotProduct;
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
            List<Device> devices = api.getDevices();
            List<IotProduct> products = api.getIotProductMap();

            System.out.println("Found " + devices.size() + " devices, " + products.size() + " products");
            for (Device dev : devices) {
                List<IotProduct> matchingProducts = products.stream()
                        .filter(prod -> dev.getDeviceClass().equals(prod.getClassId())).collect(Collectors.toList());
                if (matchingProducts.isEmpty()) {
                    System.out.println("Did not find device class for " + dev.getName());
                } else {
                    System.out.println(
                            "Device " + dev.getName() + " is a " + matchingProducts.get(0).getDefinition().name);
                }
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
